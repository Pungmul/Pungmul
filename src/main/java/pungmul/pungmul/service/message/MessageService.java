    package pungmul.pungmul.service.message;

    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.core.type.TypeReference;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import pungmul.pungmul.config.security.UserDetailsImpl;
    import pungmul.pungmul.domain.message.MessageDomainType;
    import pungmul.pungmul.domain.message.StompMessage;
    import pungmul.pungmul.domain.message.StompMessageLog;
    import pungmul.pungmul.domain.message.StompMessageResponse;
    import pungmul.pungmul.dto.Mappable;
    import pungmul.pungmul.dto.message.StompMessageDTO;
    import pungmul.pungmul.repository.message.repository.StompSubscriptionRepository;

    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.Objects;
    import java.util.stream.Stream;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class MessageService {
        private final SimpMessagingTemplate messagingTemplate;
        private final MessageHandlerRegistry handlerRegistry;
        private final MessageRouter messageRouter;
        private final StompMessageLogService stompMessageLogService;
        private final StompSubscriptionRepository stompSubscriptionRepository;
        private final StompMessageUtils stompMessageUtils;


        /**
         * 메시지를 전송하는 메서드.
         *
         * @param domainType         메시지 도메인
         * @param businessIdentifier 비즈니스 식별자
         * @param identifier         추가 식별자 (선택적)
         * @param content         메시지 내용
         * @param senderId
         */
        @Transactional
        public void sendMessage(MessageDomainType domainType, String businessIdentifier, String identifier, Object content, Long senderId) {
            // STOMP 메시지 전송 경로 구성
            String stompDest = getStompDest(domainType, businessIdentifier, identifier);
            log.info("STOMP 메시지 전송: {}", stompDest);

            List<Long> recipientUserIds = stompSubscriptionRepository.findUsersByDestination(stompDest);
            log.info("📌 메시지 수신 대상 사용자 수: {}", recipientUserIds);

            //  content 직렬화
            String serializedContent = getSerializedContent(content);
            log.info(serializedContent);

            // 3️⃣ 메시지 로그 저장 (DB에 먼저 기록하여 ID 생성)
            StompMessageLog stompMessageLog = stompMessageLogService.logStompMessageAndRecipients(
                    senderId, // sender_id (필요하면 설정 가능)
                    domainType,
                    businessIdentifier,
                    identifier,
                    stompDest,
                    serializedContent,
                    recipientUserIds
            );

            //  content 역직렬화
            Object deserializedContent = getDeserializedContent(content, serializedContent);
            log.info(deserializedContent.toString());

            // 생성된 ID를 포함하여 메시지를 STOMP로 전송
            StompMessageResponse responseMessage = new StompMessageResponse(
                    stompMessageLog.getId(), // 생성된 메시지 ID
                    domainType,
                    businessIdentifier,
                    identifier,
                    stompDest,
                    deserializedContent
            );
//            // ✅ 1️⃣ 메시지를 먼저 DB에 저장하여 ID 생성
//            StompMessageLog stompMessageLog = stompMessageLogService.logStompMessageAndRecipients(
//                    null, domainType, businessIdentifier, identifier, stompDest, content.toString(), recipientUserIds);
            log.info("STOMP 메시지 전송: {}", stompDest);
            messagingTemplate.convertAndSend(stompDest, responseMessage);
//            // ✅ STOMP 메시지 로그 저장
//            try {
//                stompMessageLogService.logStompMessage(
//                        null, // sender_id (필요하면 설정 가능)
//                        domainType,
//                        businessIdentifier,
//                        identifier,
//                        stompDest,
//                        content.toString() // Object 타입 content를 문자열로 변환
//                );
//            } catch (Exception e) {
//                log.error("STOMP 메시지 로그 저장 실패: {}", e.getMessage());
//            }
//            log.info("sub dest :{}", String.format("/sub/%s/%s/%s", domainType.getPath(), businessIdentifier.toLowerCase(), identifier));
//            // 전체 메시지 수신 경로로 전송
//            messagingTemplate.convertAndSend("/sub/all", content);
//
//            // 도메인별 메시지 수신 경로로 전송
//            messagingTemplate.convertAndSend(String.format("/sub/%s", domainType.getPath()), content);
//
//            // 도메인 및 비즈니스 식별자 기반 메시지 수신 경로로 전송
//            messagingTemplate.convertAndSend(
//                    String.format("/sub/%s/%s", domainType.getPath(), businessIdentifier.toLowerCase()),
//                    content
//            );
//
//            // 특정 식별자 기반 메시지 수신 경로로 전송 (선택적)
//            if (identifier != null && !identifier.isEmpty()) {
//                messagingTemplate.convertAndSend(
//                        String.format("/sub/%s/%s/%s", domainType.getPath(), businessIdentifier.toLowerCase(), identifier),
//                        content
//                );
//
        }

        private static Object getDeserializedContent(Object content, String serializedContent) {
            // 직렬화된 내용을 역직렬화하여 실제 객체로 변환 (responseMessage에 사용할 내용 복원)
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());  // JavaTimeModule 등록
            Object deserializedContent = null;

            try {
                if (content instanceof String) {
                    // String 타입일 경우, 그냥 직렬화된 문자열을 반환 (직렬화된 JSON을 String으로 역직렬화)
                    deserializedContent = serializedContent;  // String 타입 그대로 사용
                } else if (content instanceof Map) {
                    // content가 Map 타입일 경우 Map<String, Object>로 역직렬화
                    deserializedContent = objectMapper.readValue(serializedContent, new TypeReference<Map<String, Object>>() {});
                } else if (content instanceof List) {
                    // content가 List 타입일 경우, 배열을 List로 역직렬화
                    deserializedContent = objectMapper.readValue(serializedContent, new TypeReference<List<Object>>() {});
                } else {
                    // 그 외 객체 타입일 경우, 해당 객체로 역직렬화
                    deserializedContent = objectMapper.readValue(serializedContent, content.getClass());
                }
            } catch (JsonProcessingException e) {
                log.error("직렬화된 메시지 역직렬화 실패: {}", e.getMessage());
                throw new RuntimeException("Failed to deserialize content", e);
            }
            return deserializedContent;
        }

        private String getSerializedContent(Object content) {
            Map<String, Object> contentMap = null;

            // Mappable인 경우
            if (content instanceof Mappable) {
                contentMap = ((Mappable) content).toMap();  // Mappable인 경우 toMap() 호출
            } else if (content instanceof String) {
                // content가 문자열인 경우에는 그냥 사용 (직렬화가 필요 없으므로 그대로 사용)
                contentMap = new HashMap<>();
                contentMap.put("content", content);
            } else {
                // Mappable이 아닌 다른 경우 (필요시 더 복잡한 처리)
                contentMap = convertObjectToMap(content);  // 필요한 경우 객체를 Map으로 변환
            }

            // 직렬화
            return stompMessageUtils.serializeContent(contentMap);
        }

        public Map<String, Object> convertObjectToMap(Object obj) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(obj, Map.class);  // ObjectMapper를 통해 Map으로 변환
        }

        private static String getStompDest(MessageDomainType domainType, String businessIdentifier, String identifier) {
            List<String> pathSegments = Stream.of(domainType.getPath(), businessIdentifier, identifier)
                    .filter(Objects::nonNull) // null 값 제거
                    .filter(s -> !s.isEmpty()) // 빈 문자열 제거
                    .map(String::toLowerCase) // 소문자로 변환
                    .toList();

            return "/sub/" + String.join("/", pathSegments);
        }

        /**
         * 메시지 DTO 생성 메서드.
         */
        private StompMessage createMessage(MessageDomainType domainType,String businessIdentifier, String identifier, Object content) {
            return StompMessage.builder()
                    .domainType(domainType)
                    .businessIdentifier(businessIdentifier)
                    .identifier(identifier)
                    .content(content)
                    .build();

        }
        /**
         * 메시지 경로 생성 메서드.
         */
        private String generateMessagePath(MessageDomainType domainType, String businessIdentifier, String identifier) {
            if (identifier == null || identifier.isEmpty()) {
                return String.format("/sub/%s/%s", domainType.name().toLowerCase(), businessIdentifier.toLowerCase());
            }
            return String.format("/sub/%s/%s/%s", domainType.name().toLowerCase(), businessIdentifier.toLowerCase(), identifier);
        }

        /**
         * Stomp 메시지 처리 메서드.
         */
        public void handleStompMessage(StompMessageDTO message, UserDetailsImpl userDetails) {
//            String businessIdentifier = message.getBusinessIdentifier();
            messageRouter.routeMessage(message.getDomainType(), message, userDetails);
        }

//        public void routeMessage(StompMessageDTO messageDTO, UserDetailsImpl userDetails) {
//            StompMessage message = StompMessage.from(messageDTO);
//
//            MessageType messageType = message.getMessageType();
//            MessageDomainType domainType = message.getDomainType();
//
//            try {
//                // 적절한 핸들러 가져오기
//                MessageHandler handler = handlerRegistry.getHandler(messageType, domainType);
//
//                // 핸들러 실행
//                handler.handle(message, userDetails);
//
//            } catch (IllegalArgumentException e) {
//                log.error("Failed to route message: {}", e.getMessage());
//                throw e;
//            }
//        }
    }

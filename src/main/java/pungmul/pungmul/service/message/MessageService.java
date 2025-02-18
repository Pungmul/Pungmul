    package pungmul.pungmul.service.message;

    import jakarta.websocket.MessageHandler;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.stereotype.Service;
    import pungmul.pungmul.config.security.UserDetailsImpl;
    import pungmul.pungmul.domain.message.MessageDomainType;
    import pungmul.pungmul.domain.message.StompMessage;
    import pungmul.pungmul.domain.message.StompMessageLog;
    import pungmul.pungmul.dto.message.StompMessageDTO;

    import java.time.LocalDateTime;
    import java.util.List;
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

        /**
         * 메시지를 전송하는 메서드.
         * @param domainType 메시지 도메인
         * @param businessIdentifier 비즈니스 식별자
         * @param identifier 추가 식별자 (선택적)
         * @param content 메시지 내용
         */
        public void sendMessage(MessageDomainType domainType, String businessIdentifier, String identifier, Object content) {
            // STOMP 메시지 전송 경로 구성
            String stompDest = getStompDest(domainType, businessIdentifier, identifier);

//            // ✅ 1️⃣ 메시지를 먼저 DB에 저장하여 ID 생성
//            StompMessageLog stompMessageLog = stompMessageLogService.logStompMessageAndRecipients(
//                    null, domainType, businessIdentifier, identifier, stompDest, content.toString(), recipientUserIds);

//            // ✅ 2️⃣ 생성된 ID를 포함하여 메시지를 STOMP로 전송
//            StompMessageResponse responseMessage = new StompMessageResponse(
//                    stompMessageLog.getId(), // 생성된 메시지 ID
//                    domainType,
//                    businessIdentifier,
//                    identifier,
//                    stompDest,
//                    content.toString()
//            );

            log.info("STOMP 메시지 전송: {}", stompDest);
            messagingTemplate.convertAndSend(stompDest, content);

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
//            }
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

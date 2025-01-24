    package pungmul.pungmul.service.message;

    import jakarta.websocket.MessageHandler;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.stereotype.Service;
    import pungmul.pungmul.config.security.UserDetailsImpl;
    import pungmul.pungmul.domain.message.MessageDomainType;
    import pungmul.pungmul.domain.message.StompMessage;
    import pungmul.pungmul.dto.message.StompMessageDTO;

    import java.time.LocalDateTime;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class MessageService {
        private final SimpMessagingTemplate messagingTemplate;
        private final MessageHandlerRegistry handlerRegistry;
        private final MessageRouter messageRouter;

        /**
         * 메시지를 전송하는 메서드.
         * @param domainType 메시지 도메인
         * @param businessIdentifier 비즈니스 식별자
         * @param identifier 추가 식별자 (선택적)
         * @param content 메시지 내용
         */
        public void sendMessage(MessageDomainType domainType, String businessIdentifier, String identifier, Object content) {
            log.info("sub dest :{}", String.format("/sub/%s/%s/%s", domainType.getPath(), businessIdentifier.toLowerCase(), identifier));
            // 전체 메시지 수신 경로로 전송
            messagingTemplate.convertAndSend("/sub/all", content);

            // 도메인별 메시지 수신 경로로 전송
            messagingTemplate.convertAndSend(String.format("/sub/%s", domainType.getPath()), content);

            // 도메인 및 비즈니스 식별자 기반 메시지 수신 경로로 전송
            messagingTemplate.convertAndSend(
                    String.format("/sub/%s/%s", domainType.getPath(), businessIdentifier.toLowerCase()),
                    content
            );

            // 특정 식별자 기반 메시지 수신 경로로 전송 (선택적)
            if (identifier != null && !identifier.isEmpty()) {
                messagingTemplate.convertAndSend(
                        String.format("/sub/%s/%s/%s", domainType.getPath(), businessIdentifier.toLowerCase(), identifier),
                        content
                );
            }
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

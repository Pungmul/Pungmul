    package pungmul.pungmul.service.message;

    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.messaging.Message;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.stereotype.Service;
    import pungmul.pungmul.core.constants.MessagePaths;
    import pungmul.pungmul.domain.message.MessageType;
    import pungmul.pungmul.dto.message.DomainMessage;
    import pungmul.pungmul.dto.message.MessageDTO;

    import java.time.LocalDateTime;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class MessageService {
        private final SimpMessagingTemplate messagingTemplate;

        // messageType에 따라 발송 또는 구독 경로를 생성하고 메시지 전송
        public void sendMessage(MessageType messageType, String identifier, DomainMessage content) {
            String destinationPath = getMessagePath(messageType, identifier);
            log.info("dest path : {}", destinationPath);
            MessageDTO message = createMessage(messageType, content);
            log.info("message : {}", message.toString());
            messagingTemplate.convertAndSend(destinationPath, message);
        }

        // messageType에 따라 구독 또는 발송 경로 결정
        private String getMessagePath(MessageType messageType, String identifier) {
            switch (messageType) {
                case CHAT:
                    return generatePath(MessagePaths.CHAT, identifier, "/pub");
                case ALERT:
                case NOTIFICATION:
                    return generatePath(MessagePaths.ALARM, identifier, "/sub");
                case INVITATION:
                    return generatePath(MessagePaths.INVITATION, identifier, "/sub");
                default:
                    log.error("지원되지 않는 메시지 타입: {}", messageType);
                    throw new IllegalArgumentException("지원되지 않는 메시지 타입입니다: " + messageType);
            }
        }

        // 메시지 생성 메소드
        private MessageDTO createMessage(MessageType messageType, DomainMessage content) {
            return MessageDTO.builder()
                    .messageType(messageType)
                    .content(content)
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        // 통합 경로 생성 메서드
        public String generatePath(String domain, String identifier, String prefix) {
            return prefix + "/" + domain + "/" + identifier;
        }

//        // 구독 경로 생성 메서드
//        private String generateSubscriptionPath(String domain, String identifier) {
//            return "/sub/" + domain + "/" + identifier;
//        }
//
//        // 발송 경로 생성 메서드
//        private String generatePublishPath(String domain, String identifier) {
//            return "/pub/" + domain + "/" + identifier;
//        }
    }

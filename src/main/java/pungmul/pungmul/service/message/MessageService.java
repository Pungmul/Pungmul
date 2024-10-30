    package pungmul.pungmul.service.message;

    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.stereotype.Service;
    import pungmul.pungmul.domain.message.MessageDomainType;
    import pungmul.pungmul.domain.message.MessageType;
    import pungmul.pungmul.dto.message.DomainMessage;
    import pungmul.pungmul.dto.message.MessageDTO;

    import java.time.LocalDateTime;

    @Slf4j
    @Service
    @RequiredArgsConstructor
    public class MessageService {
        private final SimpMessagingTemplate messagingTemplate;

        // 메시지 전송 메서드
        public void sendMessage(MessageType messageType, MessageDomainType domainType, String identifier, DomainMessage content) {
            String destinationPath = generateMessagePath(messageType, domainType, identifier);
            MessageDTO message = createMessage(messageType, content);
            messagingTemplate.convertAndSend(destinationPath, message);
        }

        // 메시지 생성 메서드
        private MessageDTO createMessage(MessageType messageType, DomainMessage content) {
            return MessageDTO.builder()
                    .messageType(messageType)
                    .content(content)
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        // 메시지 경로 생성 메서드
        private String generateMessagePath(MessageType messageType, MessageDomainType domainType, String identifier) {
            String prefix = messageType == MessageType.CHAT ? "/pub" : "/sub";
            return prefix + "/" + messageType.name().toLowerCase() + "/" + domainType.name().toLowerCase() + "/" + identifier;
        }
    }

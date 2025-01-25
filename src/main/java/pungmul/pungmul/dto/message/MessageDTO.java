package pungmul.pungmul.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.message.MessageDomainType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
//    private MessageType messageType;          // 메시지 유형
    private MessageDomainType domainType;     // 메시지 도메인
    private String identifier;                // 메시지 식별자 (e.g., 모임 ID, 채팅방 ID)
    private Object content;                   // 메시지 내용 (도메인별로 다를 수 있음)
    private LocalDateTime timestamp;          // 메시지 생성 시간
}
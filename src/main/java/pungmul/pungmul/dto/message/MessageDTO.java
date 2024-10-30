package pungmul.pungmul.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.message.MessageType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private MessageType messageType;  // 메시지 타입을 enum으로 변경
    private DomainMessage content;                // 메시지 내용
    private LocalDateTime timestamp;  // 메시지 생성 시간
}
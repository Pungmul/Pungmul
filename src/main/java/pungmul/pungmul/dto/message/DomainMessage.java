package pungmul.pungmul.dto.message;

import java.time.LocalDateTime;

public interface DomainMessage {
    LocalDateTime getSentAt();
    String getMessageContent();  // 메시지의 내용 또는 설명 등
}

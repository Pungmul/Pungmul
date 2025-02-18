package pungmul.pungmul.domain.message.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StompMessageReadStatus {
    private Long id; // PK
    private Long messageId; // 메시지 ID
    private Long userId; // 사용자 ID
    private boolean isRead; // 읽음 여부
    private LocalDateTime readAt; // 읽은 시간 (읽지 않았으면 NULL)
}

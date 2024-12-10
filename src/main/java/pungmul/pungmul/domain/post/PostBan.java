package pungmul.pungmul.domain.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostBan {
    private Long id;
    private Long userId;
    private String banReason;
    private LocalDateTime banStartTime;
    private LocalDateTime banEndTime;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

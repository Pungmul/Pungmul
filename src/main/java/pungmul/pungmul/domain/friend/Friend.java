package pungmul.pungmul.domain.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Friend {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private FriendStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

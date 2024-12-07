package pungmul.pungmul.domain.member.auth;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountBan {
    private Long id;
    private String username;
    private String banReason;
    private LocalDateTime banStartTime;
    private LocalDateTime banEndTime;
    @Setter
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

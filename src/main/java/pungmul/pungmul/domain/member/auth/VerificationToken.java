package pungmul.pungmul.domain.member.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationToken {
    private Long id;
    private String token;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
}
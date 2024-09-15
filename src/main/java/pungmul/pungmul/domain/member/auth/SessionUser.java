package pungmul.pungmul.domain.member.auth;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionUser implements Serializable {
    private Long accountId;
    private Long userId;
    private String username;
    private LocalDateTime sessionCreationTime;
    private boolean isLoggedIn;
}


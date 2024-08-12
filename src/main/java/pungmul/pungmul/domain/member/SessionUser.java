package pungmul.pungmul.domain.member;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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


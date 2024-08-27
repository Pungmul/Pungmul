package pungmul.pungmul.domain.member;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtToken {
    private Long id;
    private Long accountId;
    private String token;
    private String tokenType;
    private boolean expired;
    private boolean revoked;
}

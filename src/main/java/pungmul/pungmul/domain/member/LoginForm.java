package pungmul.pungmul.domain.member;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginForm {

    private String loginId;
    private String password;
}

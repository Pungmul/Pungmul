package pungmul.pungmul.web.member.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginResponseDTO {
//    private String accessToken;
//    private String refreshToken;
//    private Long tokenExpiresIn;
    private Long accountId;
    private String userName;
    private boolean isAuthenticated; // 인증 성공 여부
    private String message;
}

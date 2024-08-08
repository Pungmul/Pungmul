package pungmul.pungmul.web.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountResponseDto {
    private String status;          // 회원가입 성공 여부 (success, fail)
    private String message;         // 응답 메시지
    private UserData data;          // 가입된 사용자 정보 (UserData로 정의된 내부 클래스)
    private String redirectUrl;     // 리다이렉트 URL

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserData {
        private Long userId;       // 회원 ID
        private String username;   // 사용자 이름 (혹은 로그인 ID)
        private String email;      // 이메일
        private String token;      // 인증 토큰 (JWT 등)
    }
}

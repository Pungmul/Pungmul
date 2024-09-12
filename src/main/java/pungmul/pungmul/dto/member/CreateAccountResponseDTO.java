package pungmul.pungmul.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountResponseDTO {
    private String loginId;      // 로그인 ID
    private String userName;     // 사용자 이름
    private String redirectUrl;  // 리다이렉트 URL
}

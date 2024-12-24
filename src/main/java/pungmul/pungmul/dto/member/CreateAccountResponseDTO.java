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
    private String username;      // 로그인 ID
    private String name;     // 사용자 이름
    private String clubName;
    private String redirectUrl;  // 리다이렉트 URL
}

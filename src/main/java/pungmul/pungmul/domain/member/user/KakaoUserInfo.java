package pungmul.pungmul.domain.member.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoUserInfo {
    private Long id; // 카카오 고유 사용자 ID
    private String nickname; // 사용자 닉네임
    private String email; // 사용자 이메일
    private String profileImage; // 프로필 이미지 URL
    private String thumbnailImage; // 썸네일 이미지 URL
    private String gender; // 성별
    private String ageRange; // 나이 범위
}

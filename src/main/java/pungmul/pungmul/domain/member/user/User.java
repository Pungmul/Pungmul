package pungmul.pungmul.domain.member.user;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    private Long id;
    private Long accountId;
    private String name;
    private String clubName;
    private LocalDate birth;
    private String email;
    private String phoneNumber;
    private Integer clubAge;
    private Gender gender;
    private String area;
    private Boolean expired;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long clubId;

    public User masked() {
        if (!Boolean.TRUE.equals(this.expired)) {
            return this; // expired가 false이면 원본 객체 반환
        }

        // expired가 true이면 개인정보 마스킹
        return User.builder()
                .id(this.id)
                .accountId(this.accountId)
                .name("탈퇴한 사용자") // 이름 마스킹
                .clubName(null) // 클럽 이름 삭제
                .birth(null) // 생년월일 삭제
                .email(this.email) // 이메일은 그대로 유지 (ID 확인 목적)
                .phoneNumber(null) // 전화번호 삭제
                .clubAge(0) // 클럽 나이 초기화
                .gender(null) // 성별 삭제
                .area(null) // 지역 정보 삭제
                .expired(this.expired)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .clubId(null) // 클럽 ID 삭제
                .build();
    }
}

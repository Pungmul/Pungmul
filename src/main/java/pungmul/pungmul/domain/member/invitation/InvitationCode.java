package pungmul.pungmul.domain.member.invitation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvitationCode {
    private Long id;
    private String code;
    private Long issuedBy;      // 코드 발급자 (null이면 운영자 발급)
    private Integer maxUses;    // 최대 사용 횟수
    private Integer remainingUses; // 남은 사용 횟수
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean canBeUsed() {
        return remainingUses > 0;
    }

    public void useCode() {
        if (remainingUses > 0) {
            remainingUses -= 1;
        } else {
            throw new IllegalStateException("초대 코드의 사용 가능 횟수가 초과되었습니다.");
        }
    }
}


package pungmul.pungmul.domain.meeting;

import lombok.Getter;

@Getter
public enum InvitationStatus {
    PENDING("대기중"),
    ACCEPTED("수락"),
    DECLINED("거절"),
    DEFERRED("보류");

    // 설명을 반환하는 메소드
    private final String description;

    // enum 생성자
    InvitationStatus(String description) {
        this.description = description;
    }

}

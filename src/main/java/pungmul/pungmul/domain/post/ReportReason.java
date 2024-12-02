package pungmul.pungmul.domain.post;

import lombok.Getter;

@Getter
public enum ReportReason {
    INCITING_TROUBLE("분란 조장"),
    PORNOGRAPHY("음란물"),
    SPAM("도배"),
    DEFAMATION("특정인 비방"),
    OFF_TOPIC("주제에 맞지 않는 게시물"),
    OTHER("기타");

    private final String description;

    ReportReason(String description) {
        this.description = description;
    }

}

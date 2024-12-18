package pungmul.pungmul.domain.lightning;

import lombok.Getter;

@Getter
public enum MeetingType {
    CLASSICPAN("정식 판굿"),
    FREEPAN("악기 자율 판굿"),
    PRACTICE("자율 연습"),
    PLAY("놀기");

    // 설명을 반환하는 메소드
    private final String description;

    // enum 생성자
    MeetingType(String description) {
        this.description = description;
    }
}

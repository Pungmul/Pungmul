package pungmul.pungmul.core.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pungmul.pungmul.core.response.ResponseCode;

@Getter
@AllArgsConstructor
public enum LightningMeetingResponseCode implements ResponseCode {

    ALREADY_JOINED_PARTICIPANT("LIGHTNING_MEETING_001", "이미 참가중인 사용자"),
    ALREADY_IN_ANOTHER_MEETING("LIGHTNING_MEETING_002", "다른 번개 모임에 참가중인 사용자"),
    NOT_JOINED_USER("LIGHTNING_MEETING_003", "해당 모임에 참가중이지 않은 사용자입니다.");

    private String code;
    private String message;
}
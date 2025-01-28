package pungmul.pungmul.core.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pungmul.pungmul.core.response.ResponseCode;

@Getter
@AllArgsConstructor
public enum LightningMeetingResponseCode implements ResponseCode {

    ALREADY_JOINED_PARTICIPANT("LIGHTNING_MEETING_001", "이미 참가중인 사용자");

    private String code;
    private String message;
}
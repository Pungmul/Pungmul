package pungmul.pungmul.core.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.core.response.ResponseCode;

@Getter
@AllArgsConstructor
public enum MemberResponseCode implements ResponseCode {
    USERNAME_ALREADY_EXISTS("MEMBER_001", "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD("MEMBER_002", "비밀번호는 8자 이상의 영문자와 숫자 조합입니다.");

    private final String code;
    private final String message;

}

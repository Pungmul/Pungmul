package pungmul.pungmul.core.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pungmul.pungmul.core.exception.custom.member.NoSuchUsernameException;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.core.response.ResponseCode;

@Getter
@AllArgsConstructor
public enum MemberResponseCode implements ResponseCode {
    INVALID_PASSWORD("MEMBER_001", "비밀번호는 8자 이상의 영문자와 숫자 조합입니다."),
    USERNAME_ALREADY_EXISTS("MEMBER_002", "이미 존재하는 이메일입니다."),
    NOTFOUND_USERNAME("MEMBER_003", "존재하지 않는 사용자 아이디입니다."),
    DELETED_ACCOUNT("MEMBER_004", "삭제된 계정입니다."),
    PAUSED_ACCOUNT("MEMBER_005", "정지된 계정입니다."),
    INVALID_INVITATION_CODE("MEMBER_006", "유효하지 않은 초대 코드입니다."),
    EXPIRED_INVITATION_CODE("MEMBER_007", "만료된 초대 코드입니다."),
    INVALID_REFRESH_TOKEN_CODE("MEMBER_008", "유효하지 않은 refresh token입니다."),
    USERNAME_NOT_FOUND("MEMBER_009", "존재하지 않는 아이디"),
    INVALID_USERNAME_OR_PASSWORD("MEMBER_010", "아이디 혹은 비밀번호가 일치하지 않습니다.");
    private final String code;
    private final String message;

}

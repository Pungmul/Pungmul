package pungmul.pungmul.core.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pungmul.pungmul.core.response.ResponseCode;

@Getter
@AllArgsConstructor
public enum PostResponseCode implements ResponseCode {

    NO_MORE_POST("POST_001", "호출할 게시물이 없습니다."),
    EXCEED_POSTING_NUM("POST_002", "금일 작성 가능한 게시물 수를 초과하였습니다."),
    FORBIDDEN_POSTING_USER("POST_003", "게시물 작성이 제한된 사용자입니다.");

    private final String code;
    private final String message;
}

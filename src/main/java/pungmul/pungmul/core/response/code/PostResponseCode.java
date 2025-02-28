package pungmul.pungmul.core.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pungmul.pungmul.core.response.ResponseCode;

@Getter
@AllArgsConstructor
public enum PostResponseCode implements ResponseCode {

    NO_MORE_POST("POST_001", "호출할 게시물이 없습니다."),
    EXCEED_POSTING_NUM("POST_002", "금일 작성 가능한 게시물 수를 초과하였습니다."),
    FORBIDDEN_POSTING_USER("POST_003", "게시물 작성이 제한된 사용자입니다."),
    NOT_POST_AUTHOR("POST_004", "게시물 작성자가 아닙니다."),
    HOT_POST_MODIFICATION("POST_005", "인기 게시물은 내용을 수정할 수 없습니다."),
    NO_SUCH_COMMENT("POST_006", "해당하는 댓글이 없습니다."),
    NOT_VALID_COMMENT_ACCESS("POST_007", "해당 댓글 접근 권한이 없습니다.");

    private final String code;
    private final String message;
}

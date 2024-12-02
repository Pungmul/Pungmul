package pungmul.pungmul.core.exception.handler;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pungmul.pungmul.core.exception.custom.member.UsernameAlreadyExistsException;
import pungmul.pungmul.core.exception.custom.post.ExceededPostingNumException;
import pungmul.pungmul.core.exception.custom.post.ForbiddenPostingUserException;
import pungmul.pungmul.core.exception.custom.post.NoMoreDataException;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.core.response.code.PostResponseCode;

@RestControllerAdvice
@Order(1)
public class PostExceptionHandler {
    @ExceptionHandler(NoMoreDataException.class)
    public ResponseEntity<BaseResponse<String>> NoMoreDataException(NoMoreDataException ex) {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(BaseResponse.ofFail(PostResponseCode.NO_MORE_POST));
    }

    @ExceptionHandler(ExceededPostingNumException.class)
    public ResponseEntity<BaseResponse<String>> ExceededPostingNumException(ExceededPostingNumException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.ofFail(PostResponseCode.EXCEED_POSTING_NUM));
    }

    @ExceptionHandler(ForbiddenPostingUserException.class)
    public ResponseEntity<BaseResponse<String>> ForbiddenPostingUserException(ForbiddenPostingUserException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.ofFail(PostResponseCode.FORBIDDEN_POSTING_USER));
    }
}

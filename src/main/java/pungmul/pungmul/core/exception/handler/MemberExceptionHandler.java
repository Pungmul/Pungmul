package pungmul.pungmul.core.exception.handler;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pungmul.pungmul.core.exception.custom.member.InvalidProfileImageException;
import pungmul.pungmul.core.exception.custom.member.UsernameAlreadyExistsException;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.ResponseCode;

public class MemberExceptionHandler {
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<String>> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(BaseResponse.ofFail(ResponseCode.USERNAME_ALREADY_EXISTS));
    }

    @ExceptionHandler(InvalidProfileImageException.class)
    public ResponseEntity<BaseResponse<String>> handleInvalidProfileImageException(InvalidProfileImageException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.ofFail(ResponseCode.INVALID_PROFILE_IMAGE));
    }
}

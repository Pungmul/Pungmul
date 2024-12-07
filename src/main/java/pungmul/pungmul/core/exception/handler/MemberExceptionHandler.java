package pungmul.pungmul.core.exception.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pungmul.pungmul.core.exception.custom.member.*;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.core.response.code.MemberResponseCode;

@Slf4j
@RestControllerAdvice
@Order(0)
public class MemberExceptionHandler {
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<String>> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(BaseResponse.ofFail(BaseResponseCode.USERNAME_ALREADY_EXISTS));
    }

    @ExceptionHandler(InvalidProfileImageException.class)
    public ResponseEntity<BaseResponse<String>> handleInvalidProfileImageException(InvalidProfileImageException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.ofFail(BaseResponseCode.INVALID_PROFILE_IMAGE));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<BaseResponse<String>> handleInvalidPassword(InvalidPasswordException ex) {
        // 예외에서 에러 메시지와 에러 코드를 가져옴

        log.info("Invalid password: {}", ex.getMessage());
        // 에러 코드를 사용해서 BaseResponse 생성
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.ofFail(MemberResponseCode.INVALID_PASSWORD));
    }

    @ExceptionHandler(NoSuchUsernameException.class)
    public ResponseEntity<BaseResponse<String>> handleNoSuchUsernameException(NoSuchUsernameException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.ofFail(MemberResponseCode.NOTFOUND_USERNAME));
    }

    @ExceptionHandler(AccountWithdrawnException.class)
    public ResponseEntity<BaseResponse<String>> handleAccountWithdrawnException(AccountWithdrawnException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.ofFail(MemberResponseCode.DELETED_ACCOUNT));
    }

    @ExceptionHandler(CustomAccountLockedException.class)
    public ResponseEntity<BaseResponse<String>> handleAccountLockedException(CustomAccountLockedException ex) {
        log.info("Account locked: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.ofFail(MemberResponseCode.PAUSED_ACCOUNT));

    }
}

package pungmul.pungmul.core.exception.handler;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pungmul.pungmul.core.exception.custom.meeting.MeetingNameAlreadyExistsException;
import pungmul.pungmul.core.exception.custom.post.NoMoreDataException;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;

@RestControllerAdvice
@Order(1)
public class MeetingExceptionHandler {
    @ExceptionHandler(MeetingNameAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<String>> MeetingNameAlreadyExistsException(MeetingNameAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.ofFail(BaseResponseCode.BAD_REQUEST, ex.getMessage()));
    }
}

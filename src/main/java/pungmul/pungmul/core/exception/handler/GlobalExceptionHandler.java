package pungmul.pungmul.core.exception.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import pungmul.pungmul.core.exception.custom.member.InvalidPasswordException;
import pungmul.pungmul.core.exception.custom.member.InvalidProfileImageException;
import pungmul.pungmul.core.exception.custom.member.TokenNotFoundException;
import pungmul.pungmul.core.exception.custom.member.UsernameAlreadyExistsException;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.core.response.code.MemberResponseCode;

import javax.naming.ServiceUnavailableException;
import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;


@Slf4j
@Aspect
@RestControllerAdvice
public final class GlobalExceptionHandler {

//    @AfterThrowing(pointcut = "execution(* pungmul.pungmul.core.exception.handler.GlobalExceptionHandler.*(..))", throwing = "ex")
//    public void logException(Exception ex) {
//        log.error("Exception caught in GlobalExceptionHandler: ", ex);
//    }

    @AfterThrowing(pointcut = "execution(* pungmul.pungmul.web..*(..))", throwing = "ex")
    public void logControllerException(Exception ex) {
        log.error("Exception occurred in controller layer: ", ex);
    }

    @AfterThrowing(pointcut = "execution(* pungmul.pungmul.service..*(..))", throwing = "ex")
        public void logServiceException(Exception ex) {
            log.error("Exception occurred in service layer: ", ex);
        }

    // 1000 - 토큰 만료

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<BaseResponse<Void>> handleExpiredJwtException(ExpiredJwtException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.EXPIRED_JWT),
                HttpStatus.UNAUTHORIZED
        );
    }
    // 1001 - 인증 필요

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<BaseResponse<Void>> handleJwtException(JwtException ex, HttpServletRequest request) {

        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.UNAUTHORIZED),
                HttpStatus.UNAUTHORIZED
        );
    }
    // 1002 - 지원되지 않는 토큰 형식

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<BaseResponse<Void>> handleUnsupportedJwtException(UnsupportedJwtException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.UNSUPPORTED_JWT),
                HttpStatus.UNAUTHORIZED
        );
    }
    // 1003 - 유효하지 않은 JWT 서명

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidJwtSignatureException(SignatureException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.INVALID_JWT_SIGNATURE),
                HttpStatus.UNAUTHORIZED
        );
    }
    // 1004 - 접근 거부

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Void>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.ACCESS_DENIED),
                HttpStatus.FORBIDDEN
        );
    }
    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleTokenNotFoundException(TokenNotFoundException ex, HttpServletRequest request) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.TOKEN_NOT_FOUND),
                HttpStatus.NOT_FOUND
        );
    }

    // 3000 - 잘못된 요청

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<Void>> handleBadRequest(MethodArgumentTypeMismatchException ex) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.BAD_REQUEST),
                HttpStatus.BAD_REQUEST
        );
    }
    // 3001 - 입력 값 검증 실패

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        boolean passwordError = false;

        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            // password 필드에서 오류가 발생하면 플래그 설정
            if ("password".equals(fieldName)) {
                passwordError = true;
                break;
            }

            errors.put(fieldName, errorMessage);
        }

        // password 필드에서 오류가 발생했으면 InvalidPasswordException 대신 바로 응답 생성
        if (passwordError) {
            return new ResponseEntity<>(
                    BaseResponse.ofFail(MemberResponseCode.INVALID_PASSWORD),
                    HttpStatus.BAD_REQUEST
            );
        }

        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.VALIDATION_FAILED),
                HttpStatus.BAD_REQUEST
        );
    }
    // 3002 - 요청한 리소스를 찾을 수 없음

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleResourceNotFoundException(NoHandlerFoundException ex) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.RESOURCE_NOT_FOUND),
                HttpStatus.NOT_FOUND
        );
    }
    // 3003 - 허용되지 않은 HTTP 메서드

    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodNotAllowedException(org.springframework.web.HttpRequestMethodNotSupportedException ex) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.METHOD_NOT_ALLOWED),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }
    // 3004 - 지원되지 않는 미디어 타입

    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<BaseResponse<Void>> handleUnsupportedMediaTypeException(org.springframework.web.HttpMediaTypeNotSupportedException ex) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.UNSUPPORTED_MEDIA_TYPE),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE
        );
    }
    // 3005 - 이미 존재하는 이메일

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<BaseResponse<Void>> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.USERNAME_ALREADY_EXISTS),
                HttpStatus.CONFLICT
        );
    }
    // 3006 - 허용되지 않는 프로필 이미지

    @ExceptionHandler(InvalidProfileImageException.class)
    public ResponseEntity<BaseResponse<Void>> handleInvalidProfileImageException(InvalidProfileImageException ex) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.INVALID_PROFILE_IMAGE),
                HttpStatus.BAD_REQUEST
        );
    }
    // 3007 - 파일 크기 초과 시 발생하는 예외 처리

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<BaseResponse<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.PAYLOAD_TOO_LARGE),
                HttpStatus.PAYLOAD_TOO_LARGE
        );
    }
    // 4000 - 서버 내부 오류

    @ExceptionHandler(RuntimeException.class)  // 포괄적인 예외 처리
    public ResponseEntity<BaseResponse<Void>> handleInternalServerError(Exception ex) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    // 4001 - 구현되지 않은 기능 요청 시

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<BaseResponse<Void>> handleNotImplemented(UnsupportedOperationException ex) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.NOT_IMPLEMENTED),
                HttpStatus.NOT_IMPLEMENTED
        );
    }
    // 4002 - 서비스 이용 불가 시

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<BaseResponse<Void>> handleServiceUnavailable(ServiceUnavailableException ex) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.SERVICE_UNAVAILABLE),
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }
    // 5000 - 데이터 무결성 위반

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.DATA_INTEGRITY_VIOLATION),
                HttpStatus.CONFLICT
        );
    }
    // 5001 - 데이터베이스 제약 조건 위반

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.CONSTRAINT_VIOLATION),
                HttpStatus.BAD_REQUEST
        );
    }
    // 6000 - 기타 예외 처리
    //  6000 - 모든 예외를 포괄적으로 처리 (개별적으로 처리되지 않은 예외들)

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<String>> handleGlobalException(Exception ex) {
        // 스택 트레이스와 함께 예외를 로그로 출력
        log.error("Exception caught: ", ex);

        // 예외 처리 및 응답 반환
        return new ResponseEntity<>(
                BaseResponse.ofFail(BaseResponseCode.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}

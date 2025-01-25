package pungmul.pungmul.core.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pungmul.pungmul.core.exception.custom.message.UnsupportedStompDomainException;
import pungmul.pungmul.core.response.code.StompResponseCode;

@RestControllerAdvice
@Order(1)
@Slf4j
public class StompExceptionHandler {
    @ExceptionHandler(UnsupportedStompDomainException.class)
    public void UnsupportedStompDomainException(UnsupportedStompDomainException ex){
        log.error(StompResponseCode.UNSUPPORTED_STOMP_DOMAIN_TYPE.toString());
    }
}

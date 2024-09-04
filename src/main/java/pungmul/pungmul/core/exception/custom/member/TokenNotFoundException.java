package pungmul.pungmul.core.exception.custom.member;

import org.springframework.http.HttpStatus;

public class TokenNotFoundException extends RuntimeException {
    private final HttpStatus status;

    public TokenNotFoundException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
    }
}
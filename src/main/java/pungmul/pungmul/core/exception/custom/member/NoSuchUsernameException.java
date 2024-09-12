package pungmul.pungmul.core.exception.custom.member;

import org.springframework.http.HttpStatus;

public class NoSuchUsernameException extends RuntimeException {
    private final HttpStatus status;

    public NoSuchUsernameException(String message) {
        super(message);
        this.status = HttpStatus.NOT_FOUND;
    }
}

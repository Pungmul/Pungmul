package pungmul.pungmul.core.exception.custom.member;

public class InvalidProfileImageException extends RuntimeException {

    public InvalidProfileImageException(String message) {
        super(message);
    }

    public InvalidProfileImageException(String message, Throwable cause) {
        super(message, cause);
    }
}
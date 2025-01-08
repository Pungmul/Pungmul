package pungmul.pungmul.core.exception.custom.member;

public class InvalidRefreshTokenException extends RuntimeException{
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}

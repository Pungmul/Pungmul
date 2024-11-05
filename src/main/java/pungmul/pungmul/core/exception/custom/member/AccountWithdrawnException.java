package pungmul.pungmul.core.exception.custom.member;

public class AccountWithdrawnException extends RuntimeException{
    public AccountWithdrawnException(String message) {
        super(message);
    }
}

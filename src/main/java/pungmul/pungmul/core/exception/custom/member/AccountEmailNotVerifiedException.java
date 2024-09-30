package pungmul.pungmul.core.exception.custom.member;

import pungmul.pungmul.core.response.ResponseCode;

public class AccountEmailNotVerifiedException extends RuntimeException{
    public AccountEmailNotVerifiedException() {
        super("이메일 인증이 진행되지 않은 계정입니다.");
    }

    public AccountEmailNotVerifiedException(String message) {
        super(message);
    }

    public AccountEmailNotVerifiedException(String message, Throwable cause) {
        super(message, cause);
    }
}

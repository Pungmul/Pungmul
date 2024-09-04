package pungmul.pungmul.core.exception.custom.member;

import pungmul.pungmul.core.response.ResponseCode;

public class InvalidPasswordException extends RuntimeException {
    private final ResponseCode responseCode;

    public InvalidPasswordException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }
}

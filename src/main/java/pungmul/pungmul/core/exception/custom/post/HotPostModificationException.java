package pungmul.pungmul.core.exception.custom.post;

import pungmul.pungmul.core.response.ResponseCode;

public class HotPostModificationException extends RuntimeException {
    public HotPostModificationException(String message) {
        super(message);
    }
}

package pungmul.pungmul.service.message.handler;

import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.message.StompMessage;

public interface StompMessageHandler {
    void handle(StompMessage message, UserDetailsImpl userDetails);
}

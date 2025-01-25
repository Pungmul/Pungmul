package pungmul.pungmul.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.exception.custom.message.UnsupportedStompDomainException;
import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.domain.message.StompMessage;
import pungmul.pungmul.service.message.handler.StompMessageHandler;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageRouter {
    private final MessageHandlerRegistry registry;

    public void routeMessage(MessageDomainType domainType, StompMessage message, UserDetailsImpl userDetails) {
        StompMessageHandler handler = registry.getHandler(domainType);
        if (handler == null) {
            log.error("No handler found for domain: {},", domainType);
            throw new UnsupportedStompDomainException();
        }
        handler.handle(message, userDetails);
    }
}

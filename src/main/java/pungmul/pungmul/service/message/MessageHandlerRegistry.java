package pungmul.pungmul.service.message;

import jakarta.annotation.PostConstruct;
import jakarta.websocket.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.service.message.handler.StompMessageHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHandlerRegistry {
    private final Map<String, StompMessageHandler> handlers = new HashMap<>();
    private final List<StompMessageHandler> messageHandlers; // Spring이 자동으로 주입

    @PostConstruct
    public void initializeHandlers() {
        for (StompMessageHandler handler : messageHandlers) {
            String key = handler.getClass().getSimpleName();
            if (handlers.containsKey(key)) {
                throw new IllegalStateException("Duplicate handler for key: " + key);
            }
            handlers.put(key, handler);
            log.info("Registered handler for key: {}", key);
        }
    }

    public void registerHandler(MessageDomainType domainType, StompMessageHandler handler) {
        handlers.put(domainType.toString(), handler);
    }

    public StompMessageHandler getHandler(MessageDomainType domainType) {
        return handlers.get(domainType.toString());
    }
}


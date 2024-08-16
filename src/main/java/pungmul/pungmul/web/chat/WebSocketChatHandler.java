package pungmul.pungmul.web.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pungmul.pungmul.dto.chat.ChatMessage;
import pungmul.pungmul.service.chat.ChatService;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketChatHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;
    private final ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Principal principal = session.getPrincipal();
        if (principal != null) {
            sessionMap.put(principal.getName(), session);  // userId 대신 username으로 매핑
            log.info("User {} has connected with session ID {}", principal.getName(), session.getId());
        } else {
            log.warn("Principal is null, unable to retrieve user information");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChatMessage chatMessage = mapper.readValue(message.getPayload(), ChatMessage.class);
        chatService.saveMessage(chatMessage);

        WebSocketSession recipientSession = sessionMap.get(chatMessage.getRecipientUsername());
        if (recipientSession != null && recipientSession.isOpen()) {
            recipientSession.sendMessage(new TextMessage(mapper.writeValueAsString(chatMessage)));
        } else {
            log.error("User {} is not connected", chatMessage.getRecipientUsername());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Principal principal = session.getPrincipal();
        if (principal != null) {
            sessionMap.remove(principal.getName());
            log.info("User {} has disconnected", principal.getName());
        }
    }

    public boolean sendMessageToUser(ChatMessage message) {
        WebSocketSession recipientSession = sessionMap.get(message.getRecipientUsername());
        if (recipientSession != null && recipientSession.isOpen()) {
            try {
                String jsonMessage = mapper.writeValueAsString(message);
                recipientSession.sendMessage(new TextMessage(jsonMessage));
                log.info("Message sent to user {}", message.getRecipientUsername());
                return true;
            } catch (IOException e) {
                log.error("Error sending message to user {}", message.getRecipientUsername(), e);
            }
        } else {
            log.error("User {} is not connected", message.getRecipientUsername());
        }
        return false;
    }

    public WebSocketSession connectUser(String username) {
        WebSocketSession session = sessionMap.get(username);
        if (session != null && session.isOpen()) {
            log.info("User {} is connected with session ID {}", username, session.getId());
            return session;
        } else {
            log.error("User {} not found or not connected", username);
            return null;
        }
    }
}

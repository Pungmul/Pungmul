package pungmul.pungmul.service.message;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class StompSessionManager {
    private final ConcurrentHashMap<String, String> sessions = new ConcurrentHashMap<>();

    public void addSession(String sessionId, String username) {
        sessions.put(sessionId, username);
    }

    public String getUsernameFromSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}

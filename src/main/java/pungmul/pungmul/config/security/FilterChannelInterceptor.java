package pungmul.pungmul.config.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import pungmul.pungmul.service.member.authorization.UserDetailsServiceImpl;
import pungmul.pungmul.service.message.StompSessionManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(99)  //Spring Security보다 우선순위 높게 설정
@RequiredArgsConstructor
@Slf4j
public class FilterChannelInterceptor implements ChannelInterceptor {
    private final TokenProvider tokenProvider;
    private final StompSessionManager stompSessionManager;

    // ✅ WebSocket 세션과 사용자 정보를 저장하는 Map (세션 유지)
//    public static Map<String, String> sessions = new ConcurrentHashMap<>();

    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        assert headerAccessor != null;

        if (headerAccessor.getCommand() == StompCommand.CONNECT) {  // Connect 요청에만 동작
            String jwtToken = String.valueOf(headerAccessor.getNativeHeader("Authorization").get(0));
            String token = jwtToken.replace("Bearer ", "");

            try {
                UserDetailsImpl userDetails = tokenProvider.getUserDetailsFromToken(token);
                if (userDetails != null) {
                    headerAccessor.addNativeHeader("username", userDetails.getUsername());
                }
            } catch (TokenExpiredException e) {
                throw new TokenExpiredException("token expired", null);
            } catch (JWTVerificationException e){
                e.printStackTrace();
            }
        }

        return message;
    }

    // ✅ WebSocket 연결이 종료되면 사용자 정보 삭제
    public void removeSession(String sessionId) {
        stompSessionManager.removeSession(sessionId);
    }

    // ✅ WebSocket 세션 ID를 기반으로 사용자 조회
    public String getUsernameBySessionId(String sessionId) {
        return stompSessionManager.getUsernameFromSession(sessionId);
    }
}

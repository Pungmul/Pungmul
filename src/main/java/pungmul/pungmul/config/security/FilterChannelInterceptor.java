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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(99)  //Spring Security보다 우선순위 높게 설정
@RequiredArgsConstructor
@Slf4j
public class FilterChannelInterceptor implements ChannelInterceptor {
    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    // ✅ WebSocket 세션과 사용자 정보를 저장하는 Map (세션 유지)
    public static Map<String, String> sessions = new ConcurrentHashMap<>();

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

//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
////        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
//        //  stomp 메세지 헤더 접근
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
//            log.info("🔐 WebSocket CONNECT 요청 감지");
//
//            String authHeader = headerAccessor.getFirstNativeHeader("Authorization");
//
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                String token = authHeader.substring(7);
//                try {
//                    if (tokenProvider.validateToken(token)) {
//                        String username = tokenProvider.getUsernameFromToken(token);
//
//                        // ✅ 세션 ID와 사용자 정보 저장
//                        String sessionId = headerAccessor.getSessionId();
//                        sessions.put(sessionId, username);
//
//                        // ✅ STOMP 헤더에 사용자 정보 추가 (나중에 구독/전송 시 사용 가능)
//                        headerAccessor.addNativeHeader("username", username);
//                        log.info("✅ WebSocket 사용자 인증 성공: {} (Session ID: {})", username, sessionId);
//                    }
//                } catch (JWTVerificationException e) {
//                    log.error("🚨 JWT 검증 실패: {}", e.getMessage());
//                }
//            } else {
//                log.warn("🚨 WebSocket CONNECT 요청에 Authorization 헤더 없음");
//            }
//        }
//        return message;
//    }

    // ✅ WebSocket 연결이 종료되면 사용자 정보 삭제
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    // ✅ WebSocket 세션 ID를 기반으로 사용자 조회
    public String getUsernameBySessionId(String sessionId) {
        return sessions.get(sessionId);
    }
}

package pungmul.pungmul.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import pungmul.pungmul.config.security.FilterChannelInterceptor;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.repository.message.repository.StompSubscriptionRepository;
import pungmul.pungmul.service.member.membermanagement.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;


@Component
@RequiredArgsConstructor
@Slf4j
public class StompEventListener {

    private final FilterChannelInterceptor filterChannelInterceptor;
    private final StompSessionManager stompSessionManager;
    private final StompSubscriptionRepository stompSubscriptionRepository;

    @EventListener
    public void handleConnectEvent(SessionConnectEvent event) {
        // 1️⃣ nativeHeaders 가져오기
        Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) event.getMessage().getHeaders().get("nativeHeaders");
        if (nativeHeaders == null) {
            log.warn("🚨 nativeHeaders가 존재하지 않습니다. 기본 인증 방식으로 진행합니다.");
            return; // 헤더가 없으면 처리를 중단
        }

        // 2️⃣ WebSocket 세션 ID 가져오기 (simpSessionId가 아니라 event.getSessionId() 사용)
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        log.info("📌 WebSocket 세션 ID: {}", sessionId);

//        String sessionId = nativeHeaders.get("simpSessionId").get(0);

        if (nativeHeaders == null || !nativeHeaders.containsKey("username"))
            throw new IllegalArgumentException("nativeHeaders에 username이 존재하지 않습니다.");


        // 2️⃣ username 값 추출
        List<String> usernameList = nativeHeaders.get("username");

        if (usernameList == null || usernameList.isEmpty())
            throw new IllegalArgumentException("username 값이 비어 있습니다.");

        String username = usernameList.get(0); // 첫 번째 값 가져오기

        if (username != null) {
            log.info("✅ WebSocket 연결 완료 - 사용자: {}, 세션 ID: {}", username, sessionId);
        } else {
            log.warn("🚨 WebSocket 연결 후 사용자 정보 없음 - 세션 ID: {}", sessionId);
        }

        stompSessionManager.addSession(sessionId, username);
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        log.info("sessionId: {}", sessionId);

        // ✅ WebSocket 세션이 종료될 때 구독 정보도 삭제
        stompSubscriptionRepository.deleteBySessionId(sessionId);
        log.info("🗑 STOMP 구독 정보 삭제 - 세션 ID: {}", sessionId);

        filterChannelInterceptor.removeSession(sessionId);
        log.info("❌ WebSocket 연결 종료 - 세션 ID: {}", sessionId);
    }

//    @EventListener
//    public void handleSubscriptionEvent(SessionSubscribeEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String sessionId = headerAccessor.getSessionId();
//        String username = filterChannelInterceptor.getUsernameBySessionId(sessionId);
//
//        if (username != null) {
//            log.info("✅ 구독 요청 - 사용자: {}, 경로: {}", username, headerAccessor.getDestination());
//        } else {
//            log.warn("🚨 구독 요청 시 사용자 정보 없음 - 세션 ID: {}", sessionId);
//        }
//    }
    /**
     * STOMP 구독 이벤트 처리 (구독 정보 저장)
     */
    @EventListener
    public void handleSubscriptionEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();

        if (destination == null) {
            log.warn("🚨 구독 요청에 destination 정보가 없음 - 세션 ID: {}", sessionId);
            return;
        }

        String username = stompSessionManager.getUsernameFromSession(sessionId);
        if (username == null) {
            log.warn("🚨 구독 요청 시 사용자 정보 없음 - 세션 ID: {}", sessionId);
            return;
        }

        Long userId = stompSubscriptionRepository.findUserIdByUsername(username);
        if (userId == null) {
            log.warn("🚨 데이터베이스에서 사용자 ID를 찾을 수 없음 - username: {}", username);
            return;
        }

        log.info("✅ 구독 요청 - 사용자: {}, 경로: {}", username, destination);
        stompSubscriptionRepository.insertSubscription(sessionId, userId, destination); // ✅ 구독 정보 저장
    }

    /**
     * STOMP 구독 취소(UNSUBSCRIBE) 이벤트 처리 (구독 정보 삭제)
     */
    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();

        if (destination == null) {
            log.warn("🚨 구독 취소 요청에 destination 정보가 없음 - 세션 ID: {}", sessionId);
            return;
        }

        String username = stompSessionManager.getUsernameFromSession(sessionId);
        if (username == null) {
            log.warn("🚨 구독 취소 요청 시 사용자 정보 없음 - 세션 ID: {}", sessionId);
            return;
        }

        Long userId = stompSubscriptionRepository.findUserIdByUsername(username);
        if (userId == null) {
            log.warn("🚨 데이터베이스에서 사용자 ID를 찾을 수 없음 - username: {}", username);
            return;
        }

        log.info("✅ 구독 취소 요청 - 사용자: {}, 경로: {}", username, destination);
        stompSubscriptionRepository.deleteSubscription(sessionId, userId, destination); // ✅ 구독 정보 삭제
    }
}

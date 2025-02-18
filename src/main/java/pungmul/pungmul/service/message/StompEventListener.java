package pungmul.pungmul.service.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import pungmul.pungmul.repository.message.repository.StompSubscriptionRepository;
import pungmul.pungmul.service.member.membermanagement.UserService;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompEventListener {
    private final StompSubscriptionRepository stompSubscriptionRepository;
    private final UserService userService;

//    /**
//     * STOMP 구독 이벤트 처리
//     */
//    @EventListener
//    public void handleSubscriptionEvent(StompHeaderAccessor headerAccessor) {
//        String sessionId = headerAccessor.getSessionId();
//        String destination = headerAccessor.getDestination();
//        Principal userPrincipal = headerAccessor.getUser();
//
//        if (userPrincipal != null && destination != null) {
//            try {
////                Long username = Long.parseLong(userPrincipal.getName()); // 사용자 ID 가져오기
//                log.info("STOMP 구독 요청 - Principal Name: {}", userPrincipal.getName());
//                Long userId = userService.getUserByEmail(userPrincipal.getName()).getId();
//                stompSubscriptionRepository.insertSubscription(sessionId, userId, destination);
//                log.info("사용자 {}가 경로 {}를 구독함 (Session ID: {})", userId, destination, sessionId);
//            } catch (NoSuchElementException e) {
//                log.error("사용자 ID 변환 실패: {}", e.getMessage());
//            }
//        }
//    }

    /**
     * ✅ STOMP 구독 이벤트 처리
     */
    @EventListener
    public void handleSubscriptionEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        Principal userPrincipal = headerAccessor.getUser();

        log.info("call sub event listener");
        log.info("userPrincipal: {}", userPrincipal.getName());
        log.info("destination: {}", destination);

        if (userPrincipal != null && destination != null) {
            try {
                log.info("STOMP 구독 요청 - Principal Name: {}", userPrincipal.getName());
                Long userId = userService.getUserByEmail(userPrincipal.getName()).getId();
                stompSubscriptionRepository.insertSubscription(sessionId, userId, destination);
                log.info("사용자 {}가 경로 {}를 구독함 (Session ID: {})", userId, destination, sessionId);
            } catch (NoSuchElementException e) {
                log.error("사용자 ID 변환 실패: {}", e.getMessage());
            }
        }
    }
    /**
     * STOMP 연결 해제 이벤트 처리
     */
    @EventListener
    public void handleDisconnectEvent(StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        stompSubscriptionRepository.deleteBySessionId(sessionId);
        log.info("세션 {}의 모든 구독 정보 삭제", sessionId);
    }
}

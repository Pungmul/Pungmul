package pungmul.pungmul.web.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import pungmul.pungmul.config.security.TokenProvider;
import pungmul.pungmul.service.member.authorization.UserDetailsServiceImpl;

import java.util.HashMap;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthController {
    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

//    @ConnectMapping
//    public void connect(StompHeaderAccessor headerAccessor) {
//        // ✅ `CONNECT` 요청에서 `Authorization` 헤더 가져오기
//        String authHeader = headerAccessor.getFirstNativeHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//
//            if (tokenProvider.validateToken(token)) {
//                String username = tokenProvider.getUsernameFromToken(token);
//                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
//
//                // ✅ WebSocket 세션 속성이 null이면 초기화
//                if (headerAccessor.getSessionAttributes() == null) {
//                    headerAccessor.setSessionAttributes(new HashMap<>());
//                }
//
//                // ✅ WebSocket 세션에 사용자 정보 저장 (로그인 상태 유지)
//                headerAccessor.getSessionAttributes().put("principal", userDetails);
//                log.info("WebSocket 사용자 인증 성공: {}", userDetails.getUsername());
//            } else {
//                log.warn("WebSocket JWT 검증 실패: 토큰이 유효하지 않음");
//            }
//        } else {
//            log.warn("WebSocket 요청에 Authorization 헤더 없음");
//        }
//    }
}

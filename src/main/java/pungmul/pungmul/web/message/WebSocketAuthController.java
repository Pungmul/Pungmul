package pungmul.pungmul.web.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import pungmul.pungmul.config.security.TokenProvider;
import pungmul.pungmul.config.security.UserDetailsImpl;
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
//        log.info("🚀 WebSocket CONNECT 요청 감지됨!");
//
//        String authHeader = headerAccessor.getFirstNativeHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            if (tokenProvider.validateToken(token)) {
//                String username = tokenProvider.getUsernameFromToken(token);
//                UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsServiceImpl.loadUserByUsername(username);
//
//                // ✅ Principal 생성하여 설정 (Spring Security의 Authentication 객체 활용)
//                UsernamePasswordAuthenticationToken authentication =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                // ✅ WebSocket 세션에서 `Principal` 설정 (이후에도 유지 가능)
//                headerAccessor.setUser(authentication);
//
//                // ✅ Spring SecurityContext에 Principal 저장 (이후에도 유지 가능)
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//
//                log.info("✅ WebSocket 사용자 인증 성공: {}", userDetails.getUsername());
//            } else {
//                log.warn("🚨 WebSocket JWT 검증 실패: 토큰이 유효하지 않음");
//            }
//        } else {
//            log.warn("🚨 WebSocket 요청에 Authorization 헤더 없음");
//        }
//    }
}

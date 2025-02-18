package pungmul.pungmul.service.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import pungmul.pungmul.config.security.TokenProvider;
import pungmul.pungmul.service.member.authorization.UserDetailsServiceImpl;

import java.util.Map;

@Slf4j
@Component
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final TokenProvider tokenProvider;

    public CustomHandshakeInterceptor(UserDetailsServiceImpl userDetailsServiceImpl, TokenProvider tokenProvider) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.tokenProvider = tokenProvider;
    }

    //    @Override
//    public boolean beforeHandshake(org.springframework.http.server.ServerHttpRequest request,
//                                   org.springframework.http.server.ServerHttpResponse response,
//                                   WebSocketHandler wsHandler,
//                                   Map<String, Object> attributes) throws Exception {
//
//        // 여기에 로그를 찍어서 요청 헤더를 확인합니다.
//        if (request instanceof org.springframework.http.server.ServletServerHttpRequest) {
//            org.springframework.http.server.ServletServerHttpRequest servletRequest =
//                    (org.springframework.http.server.ServletServerHttpRequest) request;
//
//            servletRequest.getServletRequest().getHeaderNames().asIterator().forEachRemaining(headerName ->
//                    log.info(headerName + ": " + servletRequest.getServletRequest().getHeader(headerName))
//            );
//        }
//        return true;
//    }
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        log.info("WebSocket Handshake 시작");

        // ✅ 요청의 HttpHeaders에서 Authorization 헤더 가져오기
        HttpHeaders headers = request.getHeaders();
        String authHeader = headers.getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // ✅ JWT 토큰 검증
            if (tokenProvider.validateToken(token)) {
                String username = tokenProvider.getUsernameFromToken(token);

                // ✅ 사용자 정보 로드
                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
                attributes.put("principal", userDetails);
                log.info("WebSocket 사용자 인증 성공: {}", userDetails.getUsername());
                return true;
            } else {
                log.warn("WebSocket JWT 검증 실패: 토큰이 유효하지 않음");
            }
        } else {
            log.warn("WebSocket 요청에 Authorization 헤더 없음");
        }

        return false; // ❌ 인증 실패 시 WebSocket 연결 차단
    }


    @Override
    public void afterHandshake(org.springframework.http.server.ServerHttpRequest request,
                               org.springframework.http.server.ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // 필요한 경우 추가적인 로그를 찍을 수 있습니다.
    }
}

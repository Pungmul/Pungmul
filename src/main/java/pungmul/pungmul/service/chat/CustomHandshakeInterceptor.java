package pungmul.pungmul.service.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(org.springframework.http.server.ServerHttpRequest request,
                                   org.springframework.http.server.ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        // 여기에 로그를 찍어서 요청 헤더를 확인합니다.
        if (request instanceof org.springframework.http.server.ServletServerHttpRequest) {
            org.springframework.http.server.ServletServerHttpRequest servletRequest =
                    (org.springframework.http.server.ServletServerHttpRequest) request;

            servletRequest.getServletRequest().getHeaderNames().asIterator().forEachRemaining(headerName ->
                    log.info(headerName + ": " + servletRequest.getServletRequest().getHeader(headerName))
            );
        }
        return true;
    }

    @Override
    public void afterHandshake(org.springframework.http.server.ServerHttpRequest request,
                               org.springframework.http.server.ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // 필요한 경우 추가적인 로그를 찍을 수 있습니다.
    }
}

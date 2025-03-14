package pungmul.pungmul.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import pungmul.pungmul.config.security.FilterChannelInterceptor;
import pungmul.pungmul.config.security.JwtAuthenticationProvider;
import pungmul.pungmul.config.security.TokenProvider;
import pungmul.pungmul.service.chat.CustomHandshakeInterceptor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.nio.file.AccessDeniedException;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final CustomHandshakeInterceptor handshakeInterceptor;
    private final FilterChannelInterceptor filterChannelInterceptor;

    @Value("${app.url}")
    private String appUrl;

    @Value("${stomp.test}")
    private String testUrl;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:8080", "https://jiangxy.github.io","https://pungmul.site")
            .addInterceptors(handshakeInterceptor)
            .withSockJS();

        registry.addEndpoint("/ws/alarm").setAllowedOrigins("*")
                .setAllowedOrigins(appUrl, testUrl)
                .addInterceptors(handshakeInterceptor);
        registry.addEndpoint("/ws/invitation").setAllowedOrigins("*")
                .setAllowedOrigins(appUrl, testUrl)
                .addInterceptors(handshakeInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(filterChannelInterceptor);
    }
}

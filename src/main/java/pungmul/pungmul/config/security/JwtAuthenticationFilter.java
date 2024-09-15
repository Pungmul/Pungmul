package pungmul.pungmul.config.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pungmul.pungmul.config.JwtConfig;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String jwt = null;
        String loginId = null;

        // Authorization 헤더에서 JWT 토큰 추출
        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7);
            try {
                // 토큰 검증 및 로그인 ID 추출
                loginId = tokenProvider.getUsernameFromToken(jwt);
            } catch (Exception e) {
                logger.warn("JWT validation failed: " + e.getMessage());
            }
        }
        // 로그인 ID가 있고, 현재 인증이 되어 있지 않은 경우
        if (loginId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 사용자 정보 로드
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(loginId);

            // 토큰이 유효한지 검증
            if (tokenProvider.validateToken(jwt, userDetails)) {
                // 인증 객체 생성
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 설정
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }

}

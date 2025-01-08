package pungmul.pungmul.config.security;

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

import java.io.IOException;

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
//        // 로그인 ID가 있고, 현재 인증이 되어 있지 않은 경우
//        if (loginId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            // 사용자 정보 로드
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(loginId);
//
//            // 토큰이 유효한지 검증
//            if (tokenProvider.validateToken(jwt, userDetails)) {
//                // 인증 객체 생성
//                UsernamePasswordAuthenticationToken authenticationToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                // SecurityContext에 설정
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            }
//        }

        // 토큰이 유효하지 않은 경우, Access Token이 만료되었는지 확인
        if (jwt != null && loginId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(loginId);
            if (!tokenProvider.validateToken(jwt)) {
                // Access Token 만료 처리
                String refreshToken = request.getHeader("Refresh-Token"); // 클라이언트에서 Refresh Token 전달
                if (refreshToken != null && tokenProvider.validateToken(refreshToken)) {
                    // Refresh Token이 유효한 경우, 새로운 Access Token 발급
                    loginId = tokenProvider.getUsernameFromToken(refreshToken);
                    String newAccessToken = tokenProvider.generateToken(userDetails);

                    // 응답 헤더에 새로운 Access Token 추가
                    response.setHeader("Authorization", "Bearer " + newAccessToken);

                    // 사용자 인증 처리
//                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(loginId);
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    // Refresh Token도 유효하지 않으면 Unauthorized
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } else {
                // Access Token이 유효한 경우, 사용자 인증 처리
//                UserDetails userDetails = this.userDetailsService.loadUserByUsername(loginId);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }

}

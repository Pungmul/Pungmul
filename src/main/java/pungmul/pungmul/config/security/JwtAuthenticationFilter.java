package pungmul.pungmul.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pungmul.pungmul.config.JwtConfig;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JwtConfig와 UserDetailsService 의존성 주입
    private final JwtConfig jwtConfig;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
//    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 요청 헤더에서 "Authorization" 값을 가져옴
        String header = request.getHeader("Authorization");
        String loginId = null;  // JWT에서 추출한 사용자 ID
        String jwt = null;  // JWT 토큰

        // Authorization 헤더가 존재하고 "Bearer "로 시작하는 경우
        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7);  // "Bearer " 다음의 JWT 토큰 부분을 추출

            try {
                // JWT 토큰을 파싱하여 클레임(claims)을 추출
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(jwtConfig.getSecretKey())  // JWT 서명 검증을 위한 키 설정
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                loginId = claims.getSubject();  // JWT 클레임에서 사용자 ID를 추출
            } catch (Exception e) {
                // JWT 검증 실패 시 경고 로그 출력
                logger.warn("JWT validation failed", e);
            }
        }

        // loginId가 추출되었고, 현재 SecurityContext에 인증 정보가 없는 경우
        if (loginId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // UserDetailsService를 통해 사용자 정보를 로드
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(loginId);

            // JWT에서 추출한 사용자 ID와 로드한 UserDetails의 사용자 ID가 일치하는 경우
            if (Jwts.parserBuilder().setSigningKey(jwtConfig.getSecretKey()).build().parseClaimsJws(jwt).getBody().getSubject().equals(userDetails.getUsername())) {
                // UsernamePasswordAuthenticationToken을 생성하여 사용자 인증 정보 설정
                UsernamePasswordAuthenticationToken authenticationToken  = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // 요청의 추가 정보를 설정 (IP 주소, 세션 ID 등)
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //  인증을 완료하고 authentication 객체 받음
                Authentication authentication = authenticationManager.authenticate(authenticationToken);

                // SecurityContextHolder에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // 필터 체인의 다음 필터로 요청을 전달
        filterChain.doFilter(request, response);
    }
}



package pungmul.pungmul.service.member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.config.JwtConfig;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.JwtToken;
import pungmul.pungmul.repository.member.mapper.JwtTokenMapper;
import pungmul.pungmul.repository.member.repository.JwtTokenRepository;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtTokenMapper tokenMapper;
    private final JwtTokenRepository jwtTokenRepository;
    private final JwtConfig jwtConfig;

    /**
     * 사용자와 관련된 모든 토큰을 만료 상태로 업데이트
     */
    public void revokeUserAllTokens(Account account) {
        jwtTokenRepository.revokeTokensByAccountId(account.getId());
    }

    /**
     * 새롭게 생성된 JWT 토큰을 저장
     */
    public JwtToken saveUserToken(Account account, String jwt, String tokenType) {
        JwtToken token = JwtToken.builder()
                .accountId(account.getId())
                .token(jwt)
                .tokenType(tokenType)
                .expired(false)
                .revoked(false)
                .build();
        jwtTokenRepository.save(token);
        return token;
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("bearer token: {}", bearerToken);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // "Bearer " 이후의 JWT 토큰 부분만 추출
        }

        return null;  // 토큰이 없는 경우 null 반환
    }

    public Optional<JwtToken> findByToken(String token) {
        return jwtTokenRepository.findByToken(token);
    }

    public long getExpirationTimeRemaining(String token) {
        // JWT 토큰에서 만료 시간을 추출
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtConfig.getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date expirationDate = claims.getExpiration();

        // 현재 시간과 만료 시간의 차이를 계산
        long expirationTimeRemaining = expirationDate.getTime() - System.currentTimeMillis();

        // 남은 시간을 초 단위로 반환
        return expirationTimeRemaining / 1000;
    }
}

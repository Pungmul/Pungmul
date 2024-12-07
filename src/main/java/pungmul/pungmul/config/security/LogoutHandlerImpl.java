package pungmul.pungmul.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import pungmul.pungmul.core.exception.custom.member.TokenNotFoundException;
import pungmul.pungmul.domain.member.auth.JwtToken;
import pungmul.pungmul.repository.member.repository.JwtTokenRepository;
import pungmul.pungmul.service.member.authentication.JwtTokenService;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutHandlerImpl implements LogoutHandler {
    private final JwtTokenRepository jwtTokenRepository;
    private final JwtTokenService jwtTokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = jwtTokenService.getJwtFromRequest(request);
        log.info("logout token: {}", token);

        JwtToken storedToken = jwtTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException("응애"));

        jwtTokenRepository.revokeTokenByToken(token);
    }
}

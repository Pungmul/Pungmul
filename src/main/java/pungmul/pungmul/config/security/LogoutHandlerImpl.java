package pungmul.pungmul.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pungmul.pungmul.domain.member.JwtToken;
import pungmul.pungmul.repository.member.repository.JwtTokenRepository;
import pungmul.pungmul.service.member.JwtTokenService;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 토큰"));

        jwtTokenRepository.revokeTokenByToken(token);
    }
}

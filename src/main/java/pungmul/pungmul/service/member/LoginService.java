package pungmul.pungmul.service.member;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.SessionUser;
import pungmul.pungmul.domain.member.User;
import pungmul.pungmul.repository.member.base.repository.AccountRepository;
import pungmul.pungmul.repository.member.base.repository.UserRepository;
import pungmul.pungmul.repository.member.login.LoginRepository;
import pungmul.pungmul.web.member.dto.LoginDTO;
import pungmul.pungmul.web.member.dto.LoginResponseDTO;
import pungmul.pungmul.web.member.dto.SessionConst;

import javax.naming.AuthenticationException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60 * 1000;
    private static final long REFRESH_TOKEN_VALIDITY_SECONDS = 7 * 24 * 60 * 60 * 1000;

    public LoginResponseDTO processLogin(LoginDTO loginDTO, HttpServletRequest request) throws AuthenticationException {
        HttpSession session = request.getSession(false);

        if (session != null)
            session.invalidate();

        SessionUser sessionUser = login(loginDTO);

        session = request.getSession(true);
        session.setAttribute(SessionConst.SESSION_USER, sessionUser);

        return getLoginResponseDTO(sessionUser);
    }
    public SessionUser login(LoginDTO loginDTO) throws AuthenticationException {
        Account loginAccount = accountRepository.getAccountByLoginId(loginDTO.getLoginId())
                .filter(account -> passwordEncoder.matches(loginDTO.getPassword(), account.getPassword()))
                .orElseThrow(() -> new AuthenticationException("로그인 실패"));

        return getSessionUser(loginAccount);
    }

    public SessionUser getSessionUser(Account loginAccount) {
        User loginUser = userRepository.getUserByAccountId(loginAccount.getId())
                .orElseThrow(() -> new RuntimeException("해당 User 없음"));

        return SessionUser.builder()
                .accountId(loginAccount.getId())
                .username(loginUser.getName())
                .sessionCreationTime(LocalDateTime.now()) // 현재 시간을 세션 생성 시간으로 설정
                .isLoggedIn(true) // 로그인 상태를 true로 설정
                .build();
    }

        public LoginResponseDTO getLoginResponseDTO (SessionUser sessionUser){
//            return LoginResponseDTO.builder()
//                    .accessToken(generateAccessToken(sessionUser))
//                    .refreshToken(generateRefreshToken(sessionUser))
//                    .tokenExpiresIn(getTokenExpiryTime())
//                    .accountId(sessionUser.getAccountId())
//                    .userName(sessionUser.getUsername())
//                    .build();
            return LoginResponseDTO.builder()
                    .accountId(sessionUser.getAccountId())
                    .userName(sessionUser.getUsername())
                    .isAuthenticated(true)
                    .message("로그인 성공")
                    .build();
        }

        private String generateAccessToken(SessionUser sessionUser) {
            Date now = new Date();
            Date expirtDate = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000);

            return Jwts.builder()
                    .setSubject(String.valueOf(sessionUser.getAccountId()))
                    .setIssuedAt(now)
                    .setExpiration(expirtDate)
                    .claim("username", sessionUser.getUsername())
                    .signWith(SECRET_KEY)
                    .compact();
        }

        private String generateRefreshToken(SessionUser sessionUser) {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_VALIDITY_SECONDS * 1000);

            return Jwts.builder()
                    .setSubject(String.valueOf(sessionUser.getAccountId()))
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(SECRET_KEY)
                    .compact();
        }

        private long getTokenExpiryTime() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiryTime = now.plusSeconds(ACCESS_TOKEN_VALIDITY_SECONDS / 1000);
            return java.time.Duration.between(now, expiryTime).getSeconds();
        }
}

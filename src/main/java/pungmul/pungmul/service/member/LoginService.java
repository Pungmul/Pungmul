package pungmul.pungmul.service.member;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.config.JwtConfig;
import pungmul.pungmul.config.security.TokenProvider;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.JwtToken;
import pungmul.pungmul.domain.member.SessionUser;
import pungmul.pungmul.domain.member.User;
import pungmul.pungmul.dto.member.AuthenticationResponseDTO;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.dto.member.LoginDTO;
import pungmul.pungmul.dto.member.LoginResponseDTO;
import pungmul.pungmul.config.member.SessionConst;

import javax.naming.AuthenticationException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenProvider tokenProvider;
    private final JwtTokenService jwtTokenService;

//    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
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

    @Transactional
    public AuthenticationResponseDTO authenticate(LoginDTO loginDTO) {

        //jwt, refresh token 발급
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getLoginId());
        String accessToken = tokenProvider.generateToken(userDetails);
        log.info("generated access Token : {}",accessToken);

        String refreshToken = tokenProvider.generateRefreshToken(userDetails);
        log.info("generated refresh Token : {}",refreshToken);


        //이전 토큰 무효화 및 새 토큰 저장
        Account account = accountRepository.getAccountByLoginId(loginDTO.getLoginId())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        jwtTokenService.revokeUserAllTokens(account);

        JwtToken savedAccessToken = jwtTokenService.saveUserToken(account, accessToken, JwtConfig.ACCESS_TOKEN_TYPE);
        JwtToken savedRefreshToken = jwtTokenService.saveUserToken(account, refreshToken, JwtConfig.REFRESH_TOKEN_TYPE);

        return getAuthenticationResponseDTO(accessToken, refreshToken);
    }

    private AuthenticationResponseDTO getAuthenticationResponseDTO(String accessToken, String refreshToken) {
        return AuthenticationResponseDTO.builder()
                .tokenType(JwtConfig.BEARER_TYPE)
                .accessToken(accessToken)
                .expiresIn(getTokenExpiryTime(JwtConfig.ACCESS_TOKEN_TYPE))
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn(getTokenExpiryTime(JwtConfig.REFRESH_TOKEN_TYPE))
                .build();
    }

    public void isValidCredentials(LoginDTO loginDTO) {
        Account account = accountRepository.getAccountByLoginId(loginDTO.getLoginId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (account == null || !passwordEncoder.matches(loginDTO.getPassword(), account.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public SessionUser getSessionUser(Account loginAccount) {
        User loginUser = userRepository.getUserByAccountId(loginAccount.getId())
                .orElseThrow(() -> new RuntimeException("해당 User 없음"));

        return SessionUser.builder()
                .accountId(loginAccount.getId())
                .userId(loginUser.getId())
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

//    private String generateAccessToken(SessionUser sessionUser) {
//        Date now = new Date();
//        Date expirtDate = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000);
//
//        return Jwts.builder()
//                .setSubject(String.valueOf(sessionUser.getAccountId()))
//                .setIssuedAt(now)
//                .setExpiration(expirtDate)
//                .claim("username", sessionUser.getUsername())
//                .signWith(SECRET_KEY)
//                .compact();
//    }
//
//    private String generateRefreshToken(SessionUser sessionUser) {
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_VALIDITY_SECONDS * 1000);
//
//        return Jwts.builder()
//                .setSubject(String.valueOf(sessionUser.getAccountId()))
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(SECRET_KEY)
//                .compact();
//    }

    private long getTokenExpiryTime(String tokenType) {
        LocalDateTime now = LocalDateTime.now();

        long tokenExpire = 0;
        if (tokenType.equals(JwtConfig.ACCESS_TOKEN_TYPE))
            tokenExpire = ACCESS_TOKEN_VALIDITY_SECONDS;
        else if (tokenType.equals(JwtConfig.REFRESH_TOKEN_TYPE))
            tokenExpire = REFRESH_TOKEN_VALIDITY_SECONDS;


        LocalDateTime expiryTime = now.plusSeconds(tokenExpire / 1000);
        return java.time.Duration.between(now, expiryTime).getSeconds();
    }
}


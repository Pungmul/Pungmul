package pungmul.pungmul.service.member.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.config.JwtConfig;
import pungmul.pungmul.config.security.TokenProvider;
import pungmul.pungmul.core.exception.custom.member.AccountEmailNotVerifiedException;
import pungmul.pungmul.core.exception.custom.member.AccountWithdrawnException;
import pungmul.pungmul.core.exception.custom.member.InvalidRefreshTokenException;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.domain.member.auth.SessionUser;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.dto.member.AuthenticationResponseDTO;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.dto.member.LoginDTO;
import pungmul.pungmul.dto.member.LoginResponseDTO;
import pungmul.pungmul.config.member.SessionConst;
import pungmul.pungmul.service.member.authorization.UserDetailsServiceImpl;
import pungmul.pungmul.service.member.membermanagement.AccountService;
import pungmul.pungmul.service.member.membermanagement.MemberService;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenProvider tokenProvider;
    private final JwtTokenService jwtTokenService;
    private final MemberService memberService;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    String kakaoRedirectUri;

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
        Account loginAccount = accountRepository.getAccountByUsername(loginDTO.getLoginId())
                .filter(account -> passwordEncoder.matches(loginDTO.getPassword(), account.getPassword()))
                .orElseThrow(() -> new AuthenticationException("로그인 실패"));

        return getSessionUser(loginAccount);
    }

    @Transactional
    public AuthenticationResponseDTO authenticate(String username) {
        Account enabledAccount = accountRepository.getAccountByUsernameForLogin(username)
                .orElseThrow(NoSuchElementException::new);

        if (!enabledAccount.isEnabled())
            throw new AccountEmailNotVerifiedException();

        if (enabledAccount.isWithdraw())
            throw new AccountWithdrawnException("삭제된 계정입니다.");

        if (enabledAccount.isAccountLocked()) {
            accountService.unlockAccount(enabledAccount);
        }

        //jwt, refresh token 발급
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String accessToken = tokenProvider.generateToken(userDetails);
        log.info("generated access Token : {}",accessToken);

        String refreshToken = tokenProvider.generateRefreshToken(userDetails);
        log.info("generated refresh Token : {}",refreshToken);


        //이전 토큰 무효화 및 새 토큰 저장
        Account account = accountRepository.getAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        jwtTokenService.revokeUserAllTokens(account);

        jwtTokenService.saveUserToken(account, accessToken, JwtConfig.ACCESS_TOKEN_TYPE);
        jwtTokenService.saveUserToken(account, refreshToken, JwtConfig.REFRESH_TOKEN_TYPE);

        return getAuthenticationResponseDTO(accessToken, refreshToken, username);
    }

    // Refresh Token을 사용해 Access Token 재발급
    @Transactional
    public AuthenticationResponseDTO refreshAccessToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            log.warn("Invalid or expired Refresh Token: {}", refreshToken);
            throw new InvalidRefreshTokenException("Invalid or expired Refresh Token");
        }

        try {
            // Refresh Token에서 사용자 정보 추출
            String username = tokenProvider.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 새로운 Access Token 발급
            String newAccessToken = tokenProvider.generateToken(userDetails);
            log.info("New Access Token generated for user: {}", username);

            // 저장된 Refresh Token 확인
            Account account = accountRepository.getAccountByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

            // 이전 Access Token을 무효화하고 새로운 Access Token 저장
            jwtTokenService.revokeUserAllTokens(account);
            jwtTokenService.saveUserToken(account, newAccessToken, JwtConfig.ACCESS_TOKEN_TYPE);

            return AuthenticationResponseDTO.builder()
                    .memberResponseDTO(memberService.getMemberInfo(username))
                    .tokenType(JwtConfig.BEARER_TYPE)
                    .accessToken(newAccessToken)
                    .expiresIn(getTokenExpiryTime(JwtConfig.ACCESS_TOKEN_TYPE))
                    .refreshToken(refreshToken) // 기존 Refresh Token 반환
                    .refreshTokenExpiresIn(getTokenExpiryTime(JwtConfig.REFRESH_TOKEN_TYPE))
                    .build();
        } catch (Exception e) {
            log.error("Error while refreshing access token", e);
            throw new RuntimeException("Unable to refresh access token");
        }
    }

    private AuthenticationResponseDTO getAuthenticationResponseDTO(String accessToken, String refreshToken, String username) {
        return AuthenticationResponseDTO.builder()
                .memberResponseDTO(memberService.getMemberInfo(username))
                .tokenType(JwtConfig.BEARER_TYPE)
                .accessToken(accessToken)
                .expiresIn(getTokenExpiryTime(JwtConfig.ACCESS_TOKEN_TYPE))
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn(getTokenExpiryTime(JwtConfig.REFRESH_TOKEN_TYPE))
                .build();
    }

    public void isValidCredentials(LoginDTO loginDTO) {

        Account account = accountRepository.getAccountByUsernameForLogin(loginDTO.getLoginId())
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
            return LoginResponseDTO.builder()
                    .accountId(sessionUser.getAccountId())
                    .userName(sessionUser.getUsername())
                    .isAuthenticated(true)
                    .message("로그인 성공")
                    .build();
        }

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

    public void logout(UserDetails userDetails) {

        Account account = accountRepository.getAccountByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(userDetails.getUsername()));
        jwtTokenService.revokeUserAllTokens(account);
    }
}


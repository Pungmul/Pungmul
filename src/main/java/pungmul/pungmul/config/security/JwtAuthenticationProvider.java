package pungmul.pungmul.config.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public JwtAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication;

        String username = authToken.getName();
        if (username == null) {
            throw new UsernameNotFoundException("Invalid username or password");
        }

        // UserDetailsService에게 사용자 정보 전달
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String password = userDetails.getPassword();

        // 사용자 정보 비교
        // verifyCredentials: 비밀번호를 검증하는 커스텀 메서드 - 보통 PasswordEncoder#matches 사용
        verifyCredentials(password, (String) authToken.getCredentials());

        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                userDetails.getAuthorities()
        );
    }

    private void verifyCredentials(String password, String credentials) {
        if (!passwordEncoder.matches(password, credentials)) {
            throw new BadCredentialsException("Invalid password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
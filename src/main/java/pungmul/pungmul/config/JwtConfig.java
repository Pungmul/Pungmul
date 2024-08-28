package pungmul.pungmul.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import pungmul.pungmul.config.security.JwtAuthenticationProvider;

import java.security.Key;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    public static final String ACCESS_TOKEN_TYPE = "access";
    public static final String REFRESH_TOKEN_TYPE = "refresh";

    public static final String BEARER_TYPE = "bearer";

    public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    public Key getSecretKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

}

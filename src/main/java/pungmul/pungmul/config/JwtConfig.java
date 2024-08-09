package pungmul.pungmul.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.security.Key;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    public Key getSecretKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

}

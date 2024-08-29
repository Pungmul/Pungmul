package pungmul.pungmul.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.annotation.Bean;


@Data
@Builder
@AllArgsConstructor
public class AuthenticationResponseDTO {
    private String tokenType;
    private String accessToken;
    private Long expiresIn;
    private String refreshToken;
    private Long refreshTokenExpiresIn;
}

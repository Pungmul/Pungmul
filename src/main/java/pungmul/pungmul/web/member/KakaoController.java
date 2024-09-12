package pungmul.pungmul.web.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.dto.member.AuthenticationResponseDTO;
import pungmul.pungmul.service.member.JwtTokenService;
import pungmul.pungmul.service.member.KakaoLoginService;
import pungmul.pungmul.service.member.LoginService;

import javax.naming.AuthenticationException;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/member/kakao")
public class KakaoController {
    private final KakaoLoginService kakaoLoginService;
    private final JwtTokenService jwtTokenService;

    @GetMapping("/login")
    public void kakaoLogin(HttpServletResponse response) throws AuthenticationException, IOException {
        String kakaoAuthUrl = kakaoLoginService.getKakaoAuthUrl();
        response.sendRedirect(kakaoAuthUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<BaseResponse<AuthenticationResponseDTO>>kakaoCallback(@RequestParam String code, HttpServletRequest request){

        String accessToken = kakaoLoginService.getAccessTokenFromKakao(code); // 인가 코드로 액세스 토큰 받기
        log.info("kakao access token : {}",accessToken);
        AuthenticationResponseDTO authenticationResponseDTO = kakaoLoginService.kakaoLogin(accessToken);

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, authenticationResponseDTO));
    }

    @GetMapping("/callback/postman")
    public ResponseEntity<BaseResponse<AuthenticationResponseDTO>> kakaoCallbackPostman(HttpServletRequest request){
        String accessToken = jwtTokenService.getJwtFromRequest(request);
        AuthenticationResponseDTO authenticationResponseDTO = kakaoLoginService.kakaoLogin(accessToken);

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, authenticationResponseDTO));
    }
}

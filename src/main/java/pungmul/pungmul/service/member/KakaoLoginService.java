package pungmul.pungmul.service.member;

import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pungmul.pungmul.core.exception.custom.member.NoSuchUsernameException;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.dto.member.AuthenticationResponseDTO;
import pungmul.pungmul.dto.member.KakaoTokenResponseDTO;
import pungmul.pungmul.dto.member.KakaoUserInfoResponseDTO;
import pungmul.pungmul.repository.member.repository.AccountRepository;


@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoLoginService {

    private final KakaoApiClient kakaoApiClient;
    private final AccountRepository accountRepository;
    private final LoginService loginService;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    String kakaoRedirectUri;


    public String getKakaoAuthUrl() {
        return "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+kakaoClientId+"&redirect_uri="+kakaoRedirectUri;
    }

    public AuthenticationResponseDTO kakaoLogin(String kakaoAccessToken) {
        // 카카오 사용자 정보 가져오기
        KakaoUserInfoResponseDTO kakaoUserInfo = kakaoApiClient.getUserInfo(kakaoAccessToken);

        // 카카오 사용자 정보로 DB에서 해당 계정 조회 또는 신규 계정 생성
        Account account = accountRepository.getAccountByLoginId(kakaoUserInfo.getKakaoAccount().email)
                .orElseThrow(() -> new NoSuchUsernameException("해당 사용자 없음"));

        // LoginService의 기존 로직 활용하여 JWT 토큰 발급 및 세션 처리
        return loginService.authenticate(account.getLoginId());
    }

    public String getAccessTokenFromKakao(String code) {
//        String accessToken = kakaoApiClient.getAccessToken(code, kakaoClientId, kakaoRedirectUri);
//        return accessToken;
        KakaoTokenResponseDTO kakaoTokenResponseDTO = WebClient.create("https://kauth.kakao.com")
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", kakaoClientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .bodyToMono(KakaoTokenResponseDTO.class)
                .block();

        log.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponseDTO.getAccessToken());
        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDTO.getRefreshToken());
        //제공 조건: OpenID Connect가 활성화 된 앱의 토큰 발급 요청인 경우 또는 scope에 openid를 포함한 추가 항목 동의 받기 요청을 거친 토큰 발급 요청인 경우
        log.info(" [Kakao Service] Id Token ------> {}", kakaoTokenResponseDTO.getIdToken());
        log.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponseDTO.getScope());

        return kakaoTokenResponseDTO.getAccessToken();
    }
}

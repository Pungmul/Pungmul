package pungmul.pungmul.service.member;

import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pungmul.pungmul.dto.member.KakaoUserInfoResponseDTO;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class KakaoApiClient {
    private final WebClient webClient;

    public KakaoApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://kauth.kakao.com")
                .build();
    }

    public KakaoUserInfoResponseDTO getUserInfo(String accessToken) {
        return WebClient.create("https://kapi.kakao.com")
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                .bodyToMono(KakaoUserInfoResponseDTO.class)
                .block();

    }
}

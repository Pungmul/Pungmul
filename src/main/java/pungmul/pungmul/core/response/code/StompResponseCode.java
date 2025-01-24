package pungmul.pungmul.core.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StompResponseCode {
    UNSUPPORTED_STOMP_DOMAIN_TYPE("STOMP_001", "지원되지 않는 STOMP DOMAIN 로직입니다.");

    private final String code;
    private final String message;
}

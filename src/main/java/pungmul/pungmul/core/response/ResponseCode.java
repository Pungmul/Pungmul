package pungmul.pungmul.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    // 1000 - 인증 관련
    EXPIRED_JWT("1000", "토큰이 만료되었습니다."),
    UNAUTHORIZED("1001", "인증이 필요합니다."),
    UNSUPPORTED_JWT("1002", "지원되지 않는 토큰 형식입니다."),
    INVALID_JWT_SIGNATURE("1003", "유효하지 않은 JWT 서명입니다."),
    ACCESS_DENIED("1004", "접근이 거부되었습니다."),
    TOKEN_NOT_FOUND("1005", "존재하지 않는 토큰입니다."),

    // 2000 - 성공 관련
    OK("2000", "성공"),
    CREATED("2001", "리소스가 성공적으로 생성되었습니다."),
    ACCEPTED("2002", "요청이 성공적으로 접수되었습니다."),
    NO_CONTENT("2003", "요청이 성공했으나 반환할 내용이 없습니다."),

    // 3000 - 클라이언트 오류 관련
    BAD_REQUEST("3000", "잘못된 요청입니다."),
    VALIDATION_FAILED("3001", "입력 값 검증에 실패하였습니다."),
    RESOURCE_NOT_FOUND("3002", "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED("3003", "허용되지 않은 HTTP 메서드입니다."),
    UNSUPPORTED_MEDIA_TYPE("3004", "지원되지 않는 미디어 타입입니다."),
    USERNAME_ALREADY_EXISTS("3005", "이미 존재하는 이메일입니다."),
    INVALID_PROFILE_IMAGE("3006", "허용되지 않는 프로필 이미지입니다."),
    PAYLOAD_TOO_LARGE("3007", "파일 크기 제한을 초과했습니다."),

    // 4000 - 서버 오류 관련
    INTERNAL_SERVER_ERROR("4000", "서버 내부 오류가 발생했습니다."),
    NOT_IMPLEMENTED("4001", "해당 기능은 아직 구현되지 않았습니다."),
    SERVICE_UNAVAILABLE("4002", "현재 서비스 이용이 불가능합니다."),

    // 5000 - 데이터베이스 관련
    DATA_INTEGRITY_VIOLATION("5000", "데이터 무결성 위반이 발생했습니다."),
    CONSTRAINT_VIOLATION("5001", "데이터베이스 제약 조건 위반입니다."),

    // 6000 - 기타
    UNKNOWN_ERROR("6000", "알 수 없는 오류가 발생했습니다.");

    private String code;
    private String message;
}

package pungmul.pungmul.domain.member.account;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum UserRole implements GrantedAuthority {
    ROLE_USER("ROLE_USER"),
    ROLE_REP("ROLE_REP"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_GUEST("ROLE_GUEST");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    @Override
    //  Spring Security 권한 체크는 getAuthority()의 반환 문자열 값을 기준으로 동작. 즉, USER 가 아니라 ROLE_USER와 같이 Security 규약을 지켜서 문자열을 반환해야 정상 동작
    public String getAuthority() {
        return role;
    }

}

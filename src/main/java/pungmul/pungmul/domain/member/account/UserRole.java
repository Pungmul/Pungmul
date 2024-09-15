package pungmul.pungmul.domain.member.account;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_USER("USER"),
    ROLE_REP("REP"),
    ROLE_ADMIN("ADMIN"),
    ROLE_GUEST("GUEST");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role;
    }

    public String getRole() {
        return role;
    }
}

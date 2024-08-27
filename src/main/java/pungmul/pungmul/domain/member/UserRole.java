package pungmul.pungmul.domain.member;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_USER("ROLE_USER"),
    ROLE_REP("ROLE_REP"),
    ROLE_ADMIN("ROLE_ADMIN");

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

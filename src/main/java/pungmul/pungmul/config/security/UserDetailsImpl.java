package pungmul.pungmul.config.security;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pungmul.pungmul.domain.member.account.UserRole;

import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private Long accountId;
    private String loginId;
    private String password;
    private Set<UserRole> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // 계정이 만료되지 않음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 계정이 잠기지 않음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 자격 증명이 만료되지 않음
    }

    @Override
    public boolean isEnabled() {
        return true;  // 계정이 활성화됨
    }

}

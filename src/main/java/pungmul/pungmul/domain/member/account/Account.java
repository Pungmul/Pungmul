package pungmul.pungmul.domain.member.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    private Long id;

    @NotEmpty   @Email
    private String loginId;
    @NotEmpty   @Size(min = 8, max = 20)
    private String password;

    @NotEmpty
    private Set<UserRole> roles;

    private boolean withdraw;

    @Setter //  이메일 인증 구현 전 임시
    private boolean enabled;

    private boolean accountExpired;

    @Setter
    private boolean accountLocked;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @NotNull
    private LocalDateTime lastPasswordChanged;

}

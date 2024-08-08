package pungmul.pungmul.domain.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

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

    private boolean withdraw;

    private boolean enabled;

    private boolean accountExpired;

    private boolean accountLocked;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @NotNull
    private LocalDateTime lastPasswordChanged;

}

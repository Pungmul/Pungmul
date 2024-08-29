package pungmul.pungmul.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetRoleRequestDTO {
    @NotEmpty
    @Email
    private String username;

    @NotEmpty
    private String roleName;
}

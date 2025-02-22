package pungmul.pungmul.dto.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {

    @NotEmpty @Email(message = "올바른 아이디 형식이 아닙니다.")
    private String loginId;

    @NotEmpty   @Size(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$", message = "올바른 비밀번호 형식이 아닙니다")
    private String password;
}

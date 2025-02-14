package pungmul.pungmul.dto.member;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequestDTO {
    @NotEmpty
    @Size(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,16}$", message = "8자 이상의 영문자와 숫자 조합")
    private String password;
}

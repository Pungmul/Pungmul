package pungmul.pungmul.dto.member;

import jakarta.validation.constraints.*;
import lombok.Data;
import pungmul.pungmul.domain.member.user.Gender;

import java.time.LocalDate;

@Data
public class CreateMemberRequestDTO {

    @NotEmpty @Email
    private String username;

    @NotEmpty   @Size(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,16}$", message = "8자 이상의 영문자와 숫자 조합")
    private String password;

    @NotEmpty   @Size(min = 2, max = 20)
    private String name;

    @Size(min = 1, max = 10)
    private String clubName;

    @NotEmpty @Pattern(regexp = "^[0-9]{10,15}$", message = "숫자만 입력하세요.")
    private String phoneNumber;

    private Long clubId;

    private String invitationCode;

}

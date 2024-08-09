package pungmul.pungmul.web.member.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import pungmul.pungmul.domain.member.Gender;
import pungmul.pungmul.domain.member.InstrumentStatus;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateAccountRequestDTO {

    @NotEmpty @Email
    private String loginId;

    @NotEmpty   @Size(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$", message = "8자 이상의 영문자와 숫자 조합")
    private String password;

    @NotEmpty   @Size(min = 2, max = 10)
    private String name;

    @Size(min = 1, max = 10)
    private String clubName;

    @NotNull @Past
    private LocalDate birth;

    private Integer clubAge;

    @NotNull
    private Gender gender;

    @NotEmpty @Pattern(regexp = "^[0-9]{10,15}$", message = "숫자만 입력하세요.")
    private String phoneNumber;

    @NotEmpty @Email
    private String email;

    private String area;

    private Long clubId;

    private List<InstrumentStatus> instrumentStatusList;

}

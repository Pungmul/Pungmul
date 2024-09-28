package pungmul.pungmul.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.member.user.Gender;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMemberResponseDTO {

    private String loginId;
    private String name;
    private String clubName;
    private LocalDate birth;
    private Integer clubAge;
    private Gender gender;
    private String phoneNumber;
    private String email;
    private String area;
    private List<InstrumentStatusResponseDTO> instrumentStatusDTOList;
}


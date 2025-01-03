package pungmul.pungmul.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.domain.member.user.Gender;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMemberResponseDTO {
    private String username;
    private String name;
    private String clubName;
    private String groupName;
    private String phoneNumber;
    private String email;
    private Image profile;
    private List<InstrumentStatusResponseDTO> instrumentStatusDTOList;
}


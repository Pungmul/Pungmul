package pungmul.pungmul.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.domain.member.account.UserRole;
import pungmul.pungmul.domain.member.instrument.Instrument;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchUserInfoResponseDTO {
    private String username;
    private String clubName;
    private ClubInfo clubInfo;
    private String email;
    private UserRole userRole;
    private List<String> instrumentList;
    private Image profile;
}

package pungmul.pungmul.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.member.invitation.InvitationCode;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountResponseDTO {
    private GetMemberResponseDTO getMemberDTO;
    private String invitationCode;
}

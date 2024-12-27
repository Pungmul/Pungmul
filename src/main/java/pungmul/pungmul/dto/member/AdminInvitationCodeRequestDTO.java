package pungmul.pungmul.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminInvitationCodeRequestDTO {
    private Integer maxUses;
}

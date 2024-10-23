package pungmul.pungmul.dto.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.dto.member.SimpleUserDTO;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendsToInviteResponseDTO {
    private List<SimpleUserDTO> friendsToInvite;
}

package pungmul.pungmul.dto.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.friend.Friend;
import pungmul.pungmul.dto.member.SimpleUserDTO;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendListResponseDTO {
    private List<SimpleUserDTO> friendList;
    private List<SimpleUserDTO> requestedFriendList;
}

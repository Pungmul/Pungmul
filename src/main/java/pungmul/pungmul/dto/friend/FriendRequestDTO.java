package pungmul.pungmul.dto.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.friend.FriendStatus;
import pungmul.pungmul.dto.member.SimpleUserDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDTO {
    private Long friendRequestId;
    private FriendStatus friendStatus;
    private SimpleUserDTO simpleUserDTO;
}

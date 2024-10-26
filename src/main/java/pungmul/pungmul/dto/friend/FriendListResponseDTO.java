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
    private List<FriendRequestDTO> acceptedFriendList; // 이미 친구인 목록
    private List<FriendRequestDTO> pendingSentList;    // 내가 요청한 친구 요청 목록
    private List<FriendRequestDTO> pendingReceivedList; // 내가 요청받은 친구 요청 목록
}

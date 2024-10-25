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
    private FriendStatus friendStatus; // 기존 상태 (PENDING, ACCEPTED, BLOCK 등)
    private SimpleUserDTO simpleUserDTO; // 상대방 사용자 정보
    private Boolean isRequestSentByUser; // 내가 요청을 보낸 경우 true, 받은 경우는 false. 이미 친구 상태이면 null
}

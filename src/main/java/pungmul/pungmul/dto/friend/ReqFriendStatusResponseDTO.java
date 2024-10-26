package pungmul.pungmul.dto.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.friend.FriendStatus;
import pungmul.pungmul.dto.member.SimpleUserDTO;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReqFriendStatusResponseDTO {
    private FriendStatus status;
    private SimpleUserDTO user;
    private Boolean isRequestSentByUser;  // 내가 보낸 요청인지 여부, null인 경우 응답할 필요 없음
}

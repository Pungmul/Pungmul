package pungmul.pungmul.dto.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.dto.member.SimpleUserDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailableFriendDTO {
    private SimpleUserDTO user;  // 사용자 정보
    private Boolean isRequestSentByUser;  // 내가 보낸 요청인지 여부 (null이면 요청한 적이 없는 사용자)
}

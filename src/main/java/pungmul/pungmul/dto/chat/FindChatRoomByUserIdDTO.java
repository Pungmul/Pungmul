package pungmul.pungmul.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindChatRoomByUserIdDTO {
    private Long userId;
    private String username;
    private Integer limit;
    private Integer offset;
}

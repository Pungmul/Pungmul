package pungmul.pungmul.dto.chat;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatRoomRequestDTO {
    private String receiverName;
}

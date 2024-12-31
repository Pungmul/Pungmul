package pungmul.pungmul.core.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pungmul.pungmul.core.response.ResponseCode;

@Getter
@AllArgsConstructor
public enum ChatResponseCode implements ResponseCode {
    NO_CHAT_ROOM("CHAT_001", "해당 채팅방 없음");
    private final String code;
    private final String message;
}

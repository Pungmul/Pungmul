package pungmul.pungmul.core.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pungmul.pungmul.core.exception.custom.chat.NoSuchChatroomException;
import pungmul.pungmul.core.exception.custom.post.NoMoreDataException;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.code.ChatResponseCode;
import pungmul.pungmul.core.response.code.PostResponseCode;

public class ChatExceptionHandler {
    @ExceptionHandler(NoSuchChatroomException.class)
    public ResponseEntity<BaseResponse<String>> NoSuchChatroomException(NoSuchChatroomException ex) {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(BaseResponse.ofFail(ChatResponseCode.NO_CHAT_ROOM));
    }
}

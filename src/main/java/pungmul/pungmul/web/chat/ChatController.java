package pungmul.pungmul.web.chat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;
import pungmul.pungmul.domain.member.SessionUser;
import pungmul.pungmul.dto.chat.ChatMessage;
import pungmul.pungmul.service.chat.ChatService;
import pungmul.pungmul.service.member.loginvalidation.user.User;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final WebSocketChatHandler webSocketChatHandler;
    private final ChatService chatService;

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message){
        //  메세지 처리
        return message;
    }



    @PostMapping("/connect")
    public ResponseEntity<String> connect(@User SessionUser sessionUser, @RequestParam String username) {
        WebSocketSession webSocketSession = webSocketChatHandler.connectUser(username);

        if (webSocketSession != null)
            return ResponseEntity.ok("WebSocket 연결 성공");
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("WebSocket 연결 실패");
    }

    @PostMapping("/send")
    public ResponseEntity<String> send(@User SessionUser sessionUser, @Valid @RequestBody ChatMessage chatMessage) {
        chatService.saveMessage(chatMessage);

        boolean isSent = webSocketChatHandler.sendMessageToUser(chatMessage);

        if (isSent)
            return ResponseEntity.ok("메세지 전송 성공");
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("메세지 전송 실패");

    }
}

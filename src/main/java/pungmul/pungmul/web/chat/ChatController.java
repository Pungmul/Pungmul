package pungmul.pungmul.web.chat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pungmul.pungmul.domain.member.SessionUser;
import pungmul.pungmul.domain.chat.ChatMessage;
import pungmul.pungmul.dto.chat.ChatMessageRequestDTO;
import pungmul.pungmul.dto.chat.CreateChatRoomRequestDTO;
import pungmul.pungmul.dto.chat.CreateChatRoomResponseDTO;
import pungmul.pungmul.service.chat.ChatService;
import pungmul.pungmul.service.member.loginvalidation.user.User;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    //  개인 DM 방 생성
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/personal")
    public CreateChatRoomResponseDTO createPersonalChatRoom(@RequestBody CreateChatRoomRequestDTO createChatRoomRequestDTO){
        return chatService.createPersonalChatRoom(createChatRoomRequestDTO);
    }

    //  메세지 전송
    @PreAuthorize("hasRole('USER')")
    @MessageMapping("/message")
    public ChatMessage sendMessage(ChatMessageRequestDTO chatMessageRequestDTO){
        ChatMessage chatMessage = chatService.saveMessage(chatMessageRequestDTO);
        messagingTemplate.convertAndSend("/sub/channel/" +  chatMessage.getChatRoomUUID(), chatMessage);

        return chatMessage;
    }

}

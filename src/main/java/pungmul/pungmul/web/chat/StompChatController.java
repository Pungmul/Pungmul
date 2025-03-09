package pungmul.pungmul.web.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import pungmul.pungmul.config.security.TokenProvider;
import pungmul.pungmul.domain.chat.ChatMessage;
import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.domain.message.domain.ChatBusinessIdentifier;
import pungmul.pungmul.dto.chat.ChatMessageRequestDTO;
import pungmul.pungmul.service.chat.ChatService;
import pungmul.pungmul.service.message.MessageService;
import pungmul.pungmul.service.message.StompSessionManager;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompChatController {
    private final TokenProvider tokenProvider;
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final StompSessionManager stompSessionManager;
    private final MessageService messageService;

    /*
        url : ws://localhost:8080/ws/chat
        sub dest : /sub/channel/567b5a78-6541-4dd0-9dc9-9e28239c420d
        send header : {"Authorization": "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyOEBleGFtcGxlLmNvbSIsImlhdCI6MTczNTc1NjE2OCwiZXhwIjoxNzM1NzU5NzY4fQ.GvZQFmbvPOtNy5IrlVFGZM6eLJvoQxRa672oXMicwtaoQyAiHo1tzX4csbSdTKlHk50d2Zw8H0d5YEwFkDIt5Q"}
        send dest : /pub/message
        content : {    "receiverUsername": "user10@example.com",
                       "content": "Hello, this is a test message!",
                       "chatType": "CHAT",
                       "chatRoomUUID": "567b5a78-6541-4dd0-9dc9-9e28239c420d" }
     */
//    @MessageMapping("/message")
//    public ChatMessage sendMessage(
//            @Payload ChatMessageRequestDTO chatMessageRequestDTO,
//            @Header("Authorization") String authorizationToken) {
//
//        String token = authorizationToken.replace("Bearer ", "");
//        String username = tokenProvider.getUsernameFromToken(token);
//
//        String chatRoomUUID = chatService.extractChatRoomUUIDFromDestination(chatMessageRequestDTO.getChatRoomUUID());
//        ChatMessage chatMessage = chatService.saveMessage(username, chatRoomUUID, chatMessageRequestDTO);
//        messagingTemplate.convertAndSend("/sub/channel/" + chatMessage.getChatRoomUUID(), chatMessage);
//
//        return chatMessage;
//    }

    @PreAuthorize("hasRole('USER')")
    @MessageMapping("/chat/message/{chatRoomUUID}")
    public void sendMessage(
            @Payload ChatMessageRequestDTO chatMessageRequestDTO,
            SimpMessageHeaderAccessor accessor
    ){
        log.info("call sendMessage");
        String sessionId = accessor.getSessionId();
        String username = stompSessionManager.getUsernameFromSession(sessionId);

        if(username == null)
            throw new AccessDeniedException("사용자 인증 실패 : 세션 정보가 존재하지 않음");
        chatService.sendChatMessage(chatMessageRequestDTO, username);
    }
}

package pungmul.pungmul.web.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pungmul.pungmul.config.security.TokenProvider;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.chat.ChatMessage;
import pungmul.pungmul.dto.chat.ChatMessageRequestDTO;
import pungmul.pungmul.dto.chat.CreateChatRoomRequestDTO;
import pungmul.pungmul.dto.chat.CreateChatRoomResponseDTO;
import pungmul.pungmul.service.chat.ChatService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final TokenProvider tokenProvider;

    // 개인 DM 방 생성
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/personal")
    public ResponseEntity<CreateChatRoomResponseDTO> createPersonalChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                            @RequestBody CreateChatRoomRequestDTO createChatRoomRequestDTO) {
        CreateChatRoomResponseDTO chatRoomWithRoomCheck = chatService.createChatRoomWithRoomCheck(userDetails.getLoginId(), createChatRoomRequestDTO.getReceiverName());
        return ResponseEntity.ok(chatRoomWithRoomCheck);
    }

    //  메세지 전송
//    @PreAuthorize("hasRole('USER')")
    @MessageMapping("/message/{content}")
    public ChatMessage sendMessage(
//                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @PathVariable String content,
                                    @Header("destination") String destination,
                                    @Header("Authorization") String authorizationToken){

        String token = authorizationToken.replace("Bearer ", "");

        // 토큰에서 사용자 이름 추출 (TokenProvider에 구현)
        String username = tokenProvider.getUsernameFromToken(token);
        log.info("username : {}", username );

        // 사용자 이름으로 UserDetails 객체 가져오기 (필요에 따라)
//        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);

        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessageRequestDTO chatMessageRequestDTO;
        try {
            chatMessageRequestDTO = objectMapper.readValue(content, ChatMessageRequestDTO.class);
        } catch (Exception e) {
            log.error("Failed to parse message content: {}", content, e);
            throw new RuntimeException("Invalid message format");
        }

        log.info("content : {}",chatMessageRequestDTO.getContent());

        String chatRoomUUID = chatService.extractChatRoomUUIDFromDestination(destination);
        ChatMessage chatMessage = chatService.saveMessage(username,chatRoomUUID, chatMessageRequestDTO);
        messagingTemplate.convertAndSend("/sub/channel/" +  chatMessage.getChatRoomUUID(), chatMessage);

        return chatMessage;
    }
}

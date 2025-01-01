package pungmul.pungmul.web.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pungmul.pungmul.config.security.TokenProvider;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.domain.chat.ChatMessage;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.dto.chat.ChatMessageRequestDTO;
import pungmul.pungmul.dto.chat.CreateChatRoomRequestDTO;
import pungmul.pungmul.dto.chat.CreateChatRoomResponseDTO;
import pungmul.pungmul.dto.chat.ChatRoomListResponseDTO;
import pungmul.pungmul.service.chat.ChatService;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

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

    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public ResponseEntity<BaseResponse<ChatRoomListResponseDTO>> getChatRoomList(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ChatRoomListResponseDTO chatRoomList = chatService.getChatRoomList(userDetails, page, size);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, chatRoomList));
    }
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
    @MessageMapping("/message")
//    @SendTo("/sub/channel/{chatRoomUUID}")
    public ChatMessage sendMessage(
            @Payload ChatMessageRequestDTO chatMessageRequestDTO,
            @Header("Authorization") String authorizationToken) {

        String token = authorizationToken.replace("Bearer ", "");
        String username = tokenProvider.getUsernameFromToken(token);

        String chatRoomUUID = chatService.extractChatRoomUUIDFromDestination(chatMessageRequestDTO.getChatRoomUUID());
        ChatMessage chatMessage = chatService.saveMessage(username, chatRoomUUID, chatMessageRequestDTO);
        messagingTemplate.convertAndSend("/sub/channel/" + chatMessage.getChatRoomUUID(), chatMessage);

        return chatMessage;
    }

//    //  메세지 전송
////    @PreAuthorize("hasRole('USER')")
//    @MessageMapping("/message/{content}")
//    public ChatMessage sendMessage(
//    //      @AuthenticationPrincipal UserDetailsImpl userDetails,
//            @PathVariable String content,
//            @Header("destination") String destination,
//            @Header("Authorization") String authorizationToken){
//
//        String token = authorizationToken.replace("Bearer ", "");
//
//        // 토큰에서 사용자 이름 추출 (TokenProvider에 구현)
//        String username = tokenProvider.getUsernameFromToken(token);
//        log.info("username : {}", username );
//
//        // 사용자 이름으로 UserDetails 객체 가져오기 (필요에 따라)
////        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        ChatMessageRequestDTO chatMessageRequestDTO;
//        try {
//            chatMessageRequestDTO = objectMapper.readValue(content, ChatMessageRequestDTO.class);
//        } catch (Exception e) {
//            log.error("Failed to parse message content: {}", content, e);
//            throw new RuntimeException("Invalid message format");
//        }
//
//        log.info("content : {}",chatMessageRequestDTO.getContent());
//
//        String chatRoomUUID = chatService.extractChatRoomUUIDFromDestination(destination);
//        ChatMessage chatMessage = chatService.saveMessage(username,chatRoomUUID, chatMessageRequestDTO);
//        messagingTemplate.convertAndSend("/sub/channel/" +  chatMessage.getChatRoomUUID(), chatMessage);
//
//        return chatMessage;

//    }
}

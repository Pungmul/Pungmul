package pungmul.pungmul.web.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
import pungmul.pungmul.dto.chat.*;
import pungmul.pungmul.service.chat.ChatService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final TokenProvider tokenProvider;

//    // 개인 DM 방 생성
//    @PreAuthorize("hasRole('USER')")
//    @PostMapping("/personal")
//    public ResponseEntity<BaseResponse<ChatRoomDTO>> createPersonalChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
//                                                                                    @RequestBody CreatePersonalChatRoomRequestDTO createPersonalChatRoomRequestDTO) {
//        ChatRoomDTO chatRoomWithRoomCheck = chatService.createPersonalChatRoom(userDetails.getLoginId(), createPersonalChatRoomRequestDTO.getReceiverName());
//        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.CREATED, chatRoomWithRoomCheck));
//    }

    // 개인 DM 방 생성
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/personal")
    public ResponseEntity<BaseResponse<CreateChatRoomResponseDTO>> createPersonalChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                          @RequestBody CreatePersonalChatRoomRequestDTO createPersonalChatRoomRequestDTO) {
        CreateChatRoomResponseDTO chatRoomWithRoomCheck = chatService.createPersonalChatRoom(userDetails.getLoginId(), createPersonalChatRoomRequestDTO.getReceiverName());

        if (chatRoomWithRoomCheck.getIsCreated())
            return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.CREATED, chatRoomWithRoomCheck));
        else
            return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, chatRoomWithRoomCheck));
//        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.CREATED, chatRoomWithRoomCheck));
    }

    // 단체 채팅방 생성
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/multi")
    public ResponseEntity<BaseResponse<CreateChatRoomResponseDTO>> createMultiChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                 @RequestBody CreateMultiChatRoomRequestDTO createMultiChatRoomRequestDTO) {
        CreateChatRoomResponseDTO createMultiChatRoomResponseDTO = chatService.createMultiChatRoom(userDetails.getLoginId(), createMultiChatRoomRequestDTO.getReceiverNameList());
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.CREATED, createMultiChatRoomResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public ResponseEntity<BaseResponse<ChatRoomListResponseDTO>> getChatRoomList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ChatRoomListResponseDTO chatRoomList = chatService.getChatRoomList(userDetails, page, size);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, chatRoomList));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{chatRoomUUID}/message")
    public ResponseEntity<BaseResponse<GetMessagesByChatRoomResponseDTO>> getMessagesByChatRoom(
            @PathVariable String chatRoomUUID,
            @RequestParam(defaultValue = "2", required = false) int page,
            @RequestParam(defaultValue = "20", required = false) int size) {
        GetMessagesByChatRoomResponseDTO messagesByChatRoom = chatService.getMessagesByChatRoom(chatRoomUUID, page, size);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, messagesByChatRoom));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{chatRoomUUID}")
    public ResponseEntity<BaseResponse<GetChatRoomInfoResponseDTO>> getChatRoomInfo(
            @PathVariable String chatRoomUUID,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ){
        GetChatRoomInfoResponseDTO chatRoomInfo = chatService.getChatRoomInfo(chatRoomUUID, userDetails);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, chatRoomInfo));
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
}

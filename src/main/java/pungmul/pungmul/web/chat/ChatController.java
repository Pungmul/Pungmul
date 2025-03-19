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
}

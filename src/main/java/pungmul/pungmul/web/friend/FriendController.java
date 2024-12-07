package pungmul.pungmul.web.friend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.domain.message.MessageType;
import pungmul.pungmul.dto.friend.AvailableFriendDTO;
import pungmul.pungmul.dto.friend.FriendListResponseDTO;
import pungmul.pungmul.dto.friend.FriendReqResponseDTO;
import pungmul.pungmul.dto.message.friend.FriendRequestInvitationMessageDTO;
import pungmul.pungmul.service.friend.FriendService;
import pungmul.pungmul.service.message.MessageService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {
    private final FriendService friendService;
    private final MessageService messageService;

    //  친구 목록 보기
    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public ResponseEntity<BaseResponse<FriendListResponseDTO>> getFriendList(
            @AuthenticationPrincipal UserDetails userDetails){
        FriendListResponseDTO friendList = friendService.getFriendList(userDetails);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, friendList));
    }

    //  친구 요청 가능한 사용자 목록
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/request")
    public ResponseEntity<BaseResponse<List<AvailableFriendDTO>>> searchUsers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String keyword) {
        List<AvailableFriendDTO> availableFriends = friendService.searchUsersToReqFriend(keyword, userDetails);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, availableFriends));
    }

    //  친구 신청
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/request")
    public ResponseEntity<BaseResponse<FriendReqResponseDTO>> sendFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String receiverUsername
            ){
        FriendReqResponseDTO friendReqResponseDTO = friendService.sendFriendRequest(userDetails, receiverUsername);

        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, friendReqResponseDTO));
    }

    // 친구 요청 알림을 받는 메서드
    @MessageMapping("/invitation/friend/{username}")
    public void receiveFriendRequestNotification(
            @DestinationVariable String username,
            @Payload FriendRequestInvitationMessageDTO message) {

        log.info("Friend request notification received for user: {}", username);

        // 알림 전송 경로 생성 및 메시지 전송
        messageService.sendMessage(MessageType.INVITATION, MessageDomainType.FRIEND, username, message);
    }


    //  친구 요청 수락 -> 친구 목록 창에서 특정 사용자에게 와있는 pending 상태의 친구 관계를 ACCEPTED로 변경
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/accept")
    public ResponseEntity<BaseResponse<Void>> acceptFriendRequest(
            @RequestParam Long friendRequestId
    ){
        friendService.acceptFriendRequest(friendRequestId);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.ACCEPTED));
    }

    //  친구 요청 거절 -> 친구 목록 창에서 특정 사용자에게 와있는 pending 상태의 친구 관계를 DECLINED으로 변경
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/decline")
    public ResponseEntity<BaseResponse<Void>> declineFriendRequest(
        Long friendRequestId){
        friendService.declineFriendRequest(friendRequestId);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.ACCEPTED));
    }

    //  차단 -> 친구 관계를 BLOCK으로 변경
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/block")
    public ResponseEntity<BaseResponse<Void>> blockFriend(
            Long friendRequestId){
        friendService.blockFriend(friendRequestId);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.ACCEPTED));
    }

}

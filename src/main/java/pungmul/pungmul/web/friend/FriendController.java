package pungmul.pungmul.web.friend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import pungmul.pungmul.domain.message.domain.FriendBusinessIdentifier;
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
@Tag(name= "friends", description = "친구 관련 API 모음입니다.")
@RequestMapping("/api/friends")
public class FriendController {
    private final FriendService friendService;
    private final MessageService messageService;

    //  친구 목록 보기
    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    @Operation(summary = "친구 목록 보기", description = "친구 목록을 조회한다.")
    @ApiResponse(responseCode = "2000", description = "성공")
    public ResponseEntity<BaseResponse<FriendListResponseDTO>> getFriendList(
            @AuthenticationPrincipal UserDetails userDetails){
        FriendListResponseDTO friendList = friendService.getFriendList(userDetails);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, friendList));
    }

    //  친구 요청 가능한 사용자 목록
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/request")
    @Operation(summary = "친구 요청 가능한 사용자 목록", description = "친구 요청 가능한 사용자 목록을 조회한다.")
    @ApiResponse(responseCode = "2000", description = "성공")
    public ResponseEntity<BaseResponse<List<AvailableFriendDTO>>> searchUsers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String keyword) {
        List<AvailableFriendDTO> availableFriends = friendService.searchUsersToReqFriend(keyword, userDetails);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, availableFriends));
    }

    //  친구 신청
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/request")
    @Operation(summary = "친구 신청", description = "친구 신청을 한다.")
    @ApiResponse(responseCode = "2000", description = "성공")
    public ResponseEntity<BaseResponse<FriendReqResponseDTO>> sendFriendRequest(
            @Parameter(description = "현재 인증된 사용자 정보", hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "친구 요청을 받을 사람의 이름", required = true)
            @RequestParam String receiverUsername
            ){
        FriendReqResponseDTO friendReqResponseDTO = friendService.sendFriendRequest(userDetails, receiverUsername);

        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, friendReqResponseDTO));
    }

    // 친구 요청 알림을 받는 메서드
    @MessageMapping("/friend/invitation/{username}")
    public void receiveFriendRequestNotification(
            @DestinationVariable String username,
            @Payload FriendRequestInvitationMessageDTO message) {
        log.info("Friend request notification received for user: {}", username);

        // 알림 전송 경로 생성 및 메시지 전송
        messageService.sendMessage(MessageDomainType.FRIEND, FriendBusinessIdentifier.INVITATION, username, message);
    }

    //  친구 요청 수락 -> 친구 목록 창에서 특정 사용자에게 와있는 pending 상태의 친구 관계를 ACCEPTED로 변경
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/accept")
    @Operation(summary = "친구 요청 수락", description = "친구 요청을 수락한다.")
    @ApiResponse(responseCode = "2002", description = "요청이 성공적으로 접수되었습니다.")
    public ResponseEntity<BaseResponse<Void>> acceptFriendRequest(
            @Parameter(description = "요청온 친구의 ID", required = true)
            @RequestParam Long friendRequestId
    ){
        friendService.acceptFriendRequest(friendRequestId);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.ACCEPTED));
    }

    //  친구 요청 거절 -> 친구 목록 창에서 특정 사용자에게 와있는 pending 상태의 친구 관계를 DECLINED으로 변경
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/decline")
    @Operation(summary = "친구 요청 거절", description = "친구 요청을 거절한다.")
    @ApiResponse(responseCode = "2002", description = "요청이 성공적으로 접수되었습니다.")
    public ResponseEntity<BaseResponse<Void>> declineFriendRequest(
            @Parameter(description = "요청온 친구의 ID", required = true)
        Long friendRequestId){
        friendService.declineFriendRequest(friendRequestId);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.ACCEPTED));
    }

    //  차단 -> 친구 관계를 BLOCK으로 변경
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/block")
    @Operation(summary = "친구 차단", description = "친구를 차단한다")
    @ApiResponse(responseCode = "2002", description = "요청이 성공적으로 접수되었습니다.")
    public ResponseEntity<BaseResponse<Void>> blockFriend(
            @Parameter(description = "차단할 친구의 ID", required = true)
            Long friendRequestId){
        friendService.blockFriend(friendRequestId);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.ACCEPTED));
    }
}

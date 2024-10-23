package pungmul.pungmul.web.meeting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.dto.meeting.*;
import pungmul.pungmul.repository.friend.repository.FriendRepository;
import pungmul.pungmul.service.meeting.MeetingService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting")
public class MeetingController {
    private final MeetingService meetingService;
    private final SimpMessagingTemplate messagingTemplate;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("")
    public ResponseEntity<BaseResponse<CreateMeetingResponseDTO>> createMeeting(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateMeetingRequestDTO createMeetingRequestDTO
            ){
        CreateMeetingResponseDTO meeting = meetingService.createMeeting(userDetails, createMeetingRequestDTO);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.CREATED,meeting));
    }
    @GetMapping("/invite")
    public ResponseEntity<BaseResponse<FriendsToInviteResponseDTO>> getFriendsToInvite(
            @AuthenticationPrincipal UserDetails userDetails
    ){
        FriendsToInviteResponseDTO friendsToInvite = meetingService.getFriendsToInvite(userDetails);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, friendsToInvite));
    }

//    @PreAuthorize("hasRole('USER')")
    @PostMapping("/invite")  // POST /invite 호출.
    public ResponseEntity<BaseResponse<Void>> inviteUserToMeeting(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody InviteUserToMeetingRequestDTO inviteUserToMeetingRequestDTO
    ){
        meetingService.inviteUserToMeeting(userDetails, inviteUserToMeetingRequestDTO);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/reply")
    public ResponseEntity<BaseResponse<MeetingInvitationReplyResponseDTO>> replyInvitation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MeetingInvitationReplyRequestDTO meetingInvitationReplyRequestDTO
    ){
        MeetingInvitationReplyResponseDTO reply = meetingService.replyInvitation(userDetails, meetingInvitationReplyRequestDTO);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, reply));
    }

    @MessageMapping("/meeting/{userEmail}")
    public void inviteToMeetingTest(@DestinationVariable String userEmail, String message){
        log.info("Received message : {} for user : {}", message, userEmail);

        messagingTemplate.convertAndSend("/sub/meeting/" + userEmail, message);
    }
}

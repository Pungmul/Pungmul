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
import pungmul.pungmul.dto.meeting.CreateMeetingRequestDTO;
import pungmul.pungmul.dto.meeting.CreateMeetingResponseDTO;
import pungmul.pungmul.dto.meeting.InviteUserToMeetingRequestDTO;
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

//    @PreAuthorize("hasRole('USER')")
    @GetMapping("/invite")  // GET /invite 호출.
    public ResponseEntity<BaseResponse<Void>> inviteUserToMeeting(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody InviteUserToMeetingRequestDTO inviteUserToMeetingRequestDTO
    ){
        meetingService.inviteUserToMeeting(userDetails, inviteUserToMeetingRequestDTO);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK));
    }

    @MessageMapping("/meeting/{userEmail}")
    public void inviteToMeetingTest(@DestinationVariable String userEmail, String message){
        log.info("Received message : {} for user : {}", message, userEmail);

        messagingTemplate.convertAndSend("/sub/meeting/" + userEmail, message);
    }
}

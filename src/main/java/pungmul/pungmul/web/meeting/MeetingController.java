package pungmul.pungmul.web.meeting;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.dto.meeting.CreateMeetingRequestDTO;
import pungmul.pungmul.dto.meeting.CreateMeetingResponseDTO;
import pungmul.pungmul.service.meeting.MeetingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meeting")
public class MeetingController {
    private final MeetingService meetingService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("")
    public ResponseEntity<BaseResponse<CreateMeetingResponseDTO>> createMeeting(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateMeetingRequestDTO createMeetingRequestDTO
            ){
        CreateMeetingResponseDTO meeting = meetingService.createMeeting(userDetails, createMeetingRequestDTO);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.CREATED,meeting));
    }
}

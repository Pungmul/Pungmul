package pungmul.pungmul.web.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import pungmul.pungmul.dto.lightning.AddLightningMeetingParticipantRequestDTO;
import pungmul.pungmul.dto.lightning.AddLightningMeetingParticipantResponseDTO;
import pungmul.pungmul.dto.lightning.CreateLightningMeetingRequestDTO;
import pungmul.pungmul.dto.lightning.CreateLightningMeetingResponseDTO;
import pungmul.pungmul.service.lightning.LightningMeetingService;

@Slf4j
@RequestMapping("/api/lightning")
@RequiredArgsConstructor
@RestController
public class LightningMeetingController {
    private final LightningMeetingService lightningMeetingService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/meeting")
    public ResponseEntity<BaseResponse<CreateLightningMeetingResponseDTO>> createLightningMeeting(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateLightningMeetingRequestDTO createLightningMeetingRequestDTO
    ){
        CreateLightningMeetingResponseDTO lightningMeetingResponseDTO = lightningMeetingService.createLightningMeeting(createLightningMeetingRequestDTO, userDetails);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.CREATED, lightningMeetingResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<BaseResponse<AddLightningMeetingParticipantResponseDTO>> addLightningMeetingParticipant(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddLightningMeetingParticipantRequestDTO addLightningMeetingRequestDTO
    ){
        AddLightningMeetingParticipantResponseDTO addLightningMeetingParticipantResponseDTO = lightningMeetingService.addLightningMeetingParticipant(userDetails, addLightningMeetingRequestDTO, false);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.ACCEPTED, addLightningMeetingParticipantResponseDTO));
    }

}

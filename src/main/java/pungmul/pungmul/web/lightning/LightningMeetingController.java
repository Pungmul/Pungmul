package pungmul.pungmul.web.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.dto.lightning.*;
import pungmul.pungmul.service.lightning.LightningMeetingManager;

@Slf4j
@RequestMapping("/api/lightning")
@RequiredArgsConstructor
@RestController
public class LightningMeetingController {
    private final LightningMeetingManager lightningMeetingManager;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/meeting")
    public ResponseEntity<BaseResponse<CreateLightningMeetingResponseDTO>> createLightningMeeting(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CreateLightningMeetingRequestDTO createLightningMeetingRequestDTO
    ){
        CreateLightningMeetingResponseDTO lightningMeetingResponseDTO = lightningMeetingManager.createLightningMeeting(createLightningMeetingRequestDTO, userDetails);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.CREATED, lightningMeetingResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<BaseResponse<AddLightningMeetingParticipantResponseDTO>> addLightningMeetingParticipant(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody AddLightningMeetingParticipantRequestDTO addLightningMeetingRequestDTO
    ){
        AddLightningMeetingParticipantResponseDTO addLightningMeetingParticipantResponseDTO = lightningMeetingManager.addLightningMeetingParticipant(addLightningMeetingRequestDTO, userDetails, false);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.ACCEPTED, addLightningMeetingParticipantResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<BaseResponse<WithdrawLightningMeetingResponseDTO>> withdrawLightningMeeting(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody WithdrawLightningMeetingRequestDTO withdrawLightningMeetingRequestDTO
    ){
        WithdrawLightningMeetingResponseDTO withdrawLightningMeetingResponseDTO = lightningMeetingManager.withdrawLightningMeeting(withdrawLightningMeetingRequestDTO, userDetails);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.ACCEPTED, withdrawLightningMeetingResponseDTO));
    }

}

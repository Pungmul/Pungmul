package pungmul.pungmul.web.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.dto.lightning.*;
import pungmul.pungmul.service.lightning.LightningMeetingService;

@Slf4j
@RequestMapping("/api/lightning")
@RequiredArgsConstructor
@RestController
public class LightningMeetingController {
    private final LightningMeetingService lightningMeetingService;


//    @PreAuthorize("hasRole('USER')")
//    @GetMapping("/nearby")
//    public ResponseEntity<BaseResponse<GetNearLightningMeetingResponseDTO>> getNearLightningMeeting(
//            @RequestBody GetNearLightningMeetingRequestDTO getNearLightningMeetingRequestDTO
//    ){
//        GetNearLightningMeetingResponseDTO nearLightningMeeting = lightningMeetingService.getNearLightningMeetings(getNearLightningMeetingRequestDTO);
//        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, nearLightningMeeting));
//    }

//    @PreAuthorize("hasRole('USER')")
//    @MessageMapping("/nearby")
//    public void getNearLightningMeeting(
//            @Payload GetNearLightningMeetingRequestDTO getNearLightningMeetingRequestDTO,
//            @Header("Authorization") String authorizationToken
//    ){
//        lightningMeetingService.getNearLightningMeetings(getNearLightningMeetingRequestDTO);
//    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/participants")
    public ResponseEntity<BaseResponse<GetMeetingParticipantsResponseDTO>> getMeetingParticipants(
            @RequestBody GetMeetingParticipantsRequestDTO getMeetingParticipantsRequestDTO){
        log.info("getMeetingParticipants");
        GetMeetingParticipantsResponseDTO meetingParticipants = lightningMeetingService.getMeetingParticipants(getMeetingParticipantsRequestDTO);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, meetingParticipants));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/meeting")
    public ResponseEntity<BaseResponse<CreateLightningMeetingResponseDTO>> createLightningMeeting(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CreateLightningMeetingRequestDTO createLightningMeetingRequestDTO
    ){
        CreateLightningMeetingResponseDTO lightningMeetingResponseDTO = lightningMeetingService.createLightningMeeting(createLightningMeetingRequestDTO, userDetails);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.CREATED, lightningMeetingResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<BaseResponse<AddLightningMeetingParticipantResponseDTO>> addLightningMeetingParticipant(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody AddLightningMeetingParticipantRequestDTO addLightningMeetingRequestDTO
    ){
        AddLightningMeetingParticipantResponseDTO addLightningMeetingParticipantResponseDTO = lightningMeetingService.addLightningMeetingParticipant(userDetails, addLightningMeetingRequestDTO, false);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.ACCEPTED, addLightningMeetingParticipantResponseDTO));
    }

}

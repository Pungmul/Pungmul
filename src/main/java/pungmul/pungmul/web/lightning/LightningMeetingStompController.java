package pungmul.pungmul.web.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import pungmul.pungmul.config.security.TokenProvider;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.dto.lightning.GetMeetingParticipantsRequestDTO;
import pungmul.pungmul.dto.lightning.GetNearLightningMeetingRequestDTO;
import pungmul.pungmul.service.lightning.LightningMeetingService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LightningMeetingStompController {
    private final LightningMeetingService lightningMeetingService;
    private final TokenProvider tokenProvider;
    /*
    url : ws://localhost:8080/ws/chat
    sub dest : /sub/lightning-meeting/nearby/{username}
    send header : {"Authorization": "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyOEBleGFtcGxlLmNvbSIsImlhdCI6MTczNTc1NjE2OCwiZXhwIjoxNzM1NzU5NzY4fQ.GvZQFmbvPOtNy5IrlVFGZM6eLJvoQxRa672oXMicwtaoQyAiHo1tzX4csbSdTKlHk50d2Zw8H0d5YEwFkDIt5Q"}
    send dest : /pub/lightning-meeting/nearby
    content : {   "latitude": 37.5641,   "longitude": 126.9824,   "mapLevel":4 }
    */
    @PreAuthorize("hasRole('USER')")
    @MessageMapping("/lightning-meeting/nearby")
    public void getNearLightningMeeting(
            @Payload GetNearLightningMeetingRequestDTO getNearLightningMeetingRequestDTO,
            @Header("Authorization") String authorizationToken
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        String token = authorizationToken.replace("Bearer ", "");
        UserDetailsImpl userDetails = tokenProvider.getUserDetailsFromToken(token);
        lightningMeetingService.getNearLightningMeetings(getNearLightningMeetingRequestDTO, userDetails.getUsername());
    }

    /*
    url : ws://localhost:8080/ws/chat
    sub dest : /sub/lightning-meeting/nearby/{username}
    send header : {"Authorization": "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyOEBleGFtcGxlLmNvbSIsImlhdCI6MTczNTc1NjE2OCwiZXhwIjoxNzM1NzU5NzY4fQ.GvZQFmbvPOtNy5IrlVFGZM6eLJvoQxRa672oXMicwtaoQyAiHo1tzX4csbSdTKlHk50d2Zw8H0d5YEwFkDIt5Q"}
    send dest : /pub/lightning-meeting/participants
    content : {   "latitude": 37.5641,   "longitude": 126.9824,   "mapLevel":4 }
    */
    @PreAuthorize("hasRole('USER')")
    @MessageMapping("/lightning-meeting/participants")
    public void getLightningMeetingParticipants(
            @Payload GetMeetingParticipantsRequestDTO getMeetingParticipantsRequestDTO,
            @Header("Authorization") String authorizationToken
    ){
        log.info("call getLightningMeetingParticipants");
        String token = authorizationToken.replace("Bearer ", "");
        UserDetailsImpl userDetails = tokenProvider.getUserDetailsFromToken(token);

        lightningMeetingService.getMeetingParticipants(getMeetingParticipantsRequestDTO);
    }
}

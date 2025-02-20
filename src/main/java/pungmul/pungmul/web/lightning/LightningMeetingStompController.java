package pungmul.pungmul.web.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import pungmul.pungmul.config.security.TokenProvider;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.dto.lightning.GetNearLightningMeetingRequestDTO;
import pungmul.pungmul.service.lightning.LightningMeetingManager;

import java.nio.file.AccessDeniedException;
import java.security.Principal;

import static pungmul.pungmul.config.security.FilterChannelInterceptor.sessions;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LightningMeetingStompController {
    private final LightningMeetingManager lightningMeetingManager;
    private final TokenProvider tokenProvider;
    /*
    url : ws://localhost:8080/ws
    sub dest : /sub/lightning-meeting/nearby/{username}
    send header : {"Authorization": "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyOEBleGFtcGxlLmNvbSIsImlhdCI6MTczNTc1NjE2OCwiZXhwIjoxNzM1NzU5NzY4fQ.GvZQFmbvPOtNy5IrlVFGZM6eLJvoQxRa672oXMicwtaoQyAiHo1tzX4csbSdTKlHk50d2Zw8H0d5YEwFkDIt5Q"}
    send dest : /pub/lightning-meeting/nearby
    content : {   "latitude": 37.5641,   "longitude": 126.9824,   "mapLevel":4 }
    */
//    @PreAuthorize("hasRole('USER')")
//    @MessageMapping("/lightning-meeting/nearby")
//    public void getNearLightningMeeting(
//            @Payload GetNearLightningMeetingRequestDTO getNearLightningMeetingRequestDTO,
//            Principal principal
////            @Header("Authorization") String authorizationToken
////            @AuthenticationPrincipal UserDetailsImpl userDetails
//    ) {
////        String token = authorizationToken.replace("Bearer ", "");
////        UserDetailsImpl userDetails = tokenProvider.getUserDetailsFromToken(token);
//        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
//        lightningMeetingManager.getNearLightningMeetings(getNearLightningMeetingRequestDTO, userDetails.getUsername());
//    }

    @PreAuthorize("hasRole('USER')")
    @MessageMapping("/lightning-meeting/nearby")
    public void getNearLightningMeeting(
            @Payload GetNearLightningMeetingRequestDTO getNearLightningMeetingRequestDTO,
            SimpMessageHeaderAccessor accessor
    ) throws AccessDeniedException {
        String sessionId = accessor.getSessionId();
        String username = sessions.get(sessionId);

        if (username == null) {
            throw new AccessDeniedException("사용자 인증 실패: 세션 정보가 존재하지 않습니다.");
        }

        log.info("✅ 사용자 {}가 번개 모임 조회 요청을 보냈습니다.", username);
        lightningMeetingManager.getNearLightningMeetings(getNearLightningMeetingRequestDTO, username);
    }

    /*
    url : ws://localhost:8080/ws
    sub dest : /sub/lightning-meeting/participants/{meetingId}
    send header : {"Authorization": "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyOEBleGFtcGxlLmNvbSIsImlhdCI6MTczNTc1NjE2OCwiZXhwIjoxNzM1NzU5NzY4fQ.GvZQFmbvPOtNy5IrlVFGZM6eLJvoQxRa672oXMicwtaoQyAiHo1tzX4csbSdTKlHk50d2Zw8H0d5YEwFkDIt5Q"}
    send dest : /pub/lightning-meeting/participants/{meetingId}
    */
    @PreAuthorize("hasRole('USER')")
    @MessageMapping("/lightning-meeting/participants/{meetingId}")
    public void getLightningMeetingParticipants(
            @DestinationVariable Long meetingId,
//            @Payload GetMeetingParticipantsRequestDTO getMeetingParticipantsRequestDTO,
            @Header("Authorization") String authorizationToken
    ){
        log.info("call getLightningMeetingParticipants");
        log.info("meetingId : {}", meetingId);
        String token = authorizationToken.replace("Bearer ", "");
        UserDetailsImpl userDetails = tokenProvider.getUserDetailsFromToken(token);

        lightningMeetingManager.getLightningMeetingParticipants(meetingId);
    }
}

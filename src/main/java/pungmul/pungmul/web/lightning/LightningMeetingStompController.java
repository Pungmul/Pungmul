package pungmul.pungmul.web.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import pungmul.pungmul.config.security.TokenProvider;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.dto.lightning.GetNearLightningMeetingRequestDTO;
import pungmul.pungmul.service.lightning.LightningMeetingService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LightningMeetingStompController {
    private final LightningMeetingService lightningMeetingService;
    private final TokenProvider tokenProvider;

    @PreAuthorize("hasRole('USER')")
    @MessageMapping("/lightning-meeting/nearby")
    public void getNearLightningMeeting(
            @Payload GetNearLightningMeetingRequestDTO getNearLightningMeetingRequestDTO,
            @Header("Authorization") String authorizationToken
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        String token = authorizationToken.replace("Bearer ", "");
        String username = tokenProvider.getUsernameFromToken(token);
        lightningMeetingService.getNearLightningMeetings(getNearLightningMeetingRequestDTO, username);
    }
}

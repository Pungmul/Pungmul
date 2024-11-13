package pungmul.pungmul.web.lightning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pungmul.pungmul.core.response.BaseResponse;

import java.nio.file.attribute.UserPrincipal;

@Slf4j
@RequestMapping("/lightning")
@RequiredArgsConstructor
@RestController
public class LightningMeetingController {

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<BaseResponse<CreateLightningMeetingResponseDTO>> createLightningMeeting(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CreateLightningMeetingRequestDTO createLightningMeetingRequestDTO
    ){

    }
}

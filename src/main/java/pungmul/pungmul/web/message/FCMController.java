package pungmul.pungmul.web.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.domain.message.FCMMessageLog;
import pungmul.pungmul.dto.message.FCMTokenRequestDTO;
import pungmul.pungmul.dto.message.UpdateFCMTokenDTO;
import pungmul.pungmul.service.message.FCMService;

import java.util.List;

@Slf4j
@RequestMapping("/api/message/fcm")
@RequiredArgsConstructor
@Controller
public class FCMController {
    private final FCMService fcmService;

    @PostMapping("/save")
    public ResponseEntity<BaseResponse<Void>> saveFCMToken(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody FCMTokenRequestDTO fcmTokenRequestDTO) {
        fcmService.saveFCMToken(userDetails, fcmTokenRequestDTO);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK));
    }
    // 2. Retrieve FCM Tokens for a User
    @GetMapping("/tokens/{userId}")
    public ResponseEntity<BaseResponse<List<String>>> getTokensByUserId(@PathVariable Long userId) {
        List<String> tokens = fcmService.getTokensByUserId(userId);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, tokens));
    }

    // 3. Invalidate a Token
    @PatchMapping("/invalidate-token")
    public ResponseEntity<BaseResponse<Void>> invalidateToken(@RequestBody UpdateFCMTokenDTO updateFCMTokenDTO) {
        fcmService.invalidateFCMToken(updateFCMTokenDTO.getToken());
        log.info("FCM token invalidated: {}", updateFCMTokenDTO.getToken());
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK));
    }

    // 4. Get All Valid Tokens
    @GetMapping("/valid-tokens")
    public ResponseEntity<BaseResponse<List<String>>> getAllValidTokens() {
        List<String> validTokens = fcmService.getAllValidTokens();
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, validTokens));
    }

    @GetMapping("/logs/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<FCMMessageLog>>> getAllLogs() {
        List<FCMMessageLog> allFCMLogs = fcmService.getAllFCMLogs();
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, allFCMLogs));
    }

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<FCMMessageLog>>> getFCMLogsByUserId(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<FCMMessageLog> fcmLogsByUserId = fcmService.getFCMLogsByReceiverId(userDetails);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, fcmLogsByUserId));
    }

}

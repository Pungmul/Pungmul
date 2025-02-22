package pungmul.pungmul.web.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.domain.message.StompMessageLog;
import pungmul.pungmul.domain.message.domain.StompMessageReadStatus;
import pungmul.pungmul.dto.message.MessageReadRequestDTO;
import pungmul.pungmul.dto.message.StompMessageDTO;
import pungmul.pungmul.service.message.MessageService;
import pungmul.pungmul.service.message.StompMessageLogService;
import pungmul.pungmul.service.message.StompMessageStatusService;
import pungmul.pungmul.service.message.StompSessionManager;

import java.util.List;

@RequestMapping("/api/message/stomp")
@Controller
@RequiredArgsConstructor
@Slf4j
public class StompController {
    private final MessageService messageService;
    private final StompMessageLogService stompMessageLogService;
    private final StompMessageStatusService stompMessageStatusService;
    private final StompSessionManager stompSessionManager;

//    @MessageMapping("/all")
//    public void handleStompMessage(
//            @Payload @Validated StompMessageDTO stompMessage,
////            @Header("Authorization") String authorizationToken,
//            @AuthenticationPrincipal UserDetailsImpl userDetails
//            ){
//        log.info("Handling stomp message: {}", stompMessage);
//        messageService.handleStompMessage(stompMessage, userDetails);
//    }
    /**
     * STOMP 메시지를 읽음 처리
     */
    @MessageMapping("/stomp/mark")
    public void markMessageAsRead(
            @Payload MessageReadRequestDTO messageReadRequestDTO,
            SimpMessageHeaderAccessor accessor
            ) {
        log.info("markMessageAsRead {}", messageReadRequestDTO.getMessageIdList());
        String sessionId = accessor.getSessionId();
        String username = stompSessionManager.getUsernameFromSession(sessionId);
        stompMessageStatusService.markMessagesAsRead(messageReadRequestDTO.getMessageIdList(), username);
    }
//
//    /**
//     * 특정 도메인 및 비즈니스 식별자를 기반으로 메시지 로그 조회
//     */
//    @GetMapping("/logs")
//    public List<StompMessageLog> getLogsByDomainAndBusinessIdentifier(
//            @RequestParam MessageDomainType domainType, @RequestParam String businessIdentifier) {
//        return stompMessageLogService.getLogsByDomainAndBusinessIdentifier(domainType, businessIdentifier);
//    }
//
    /**
     * 특정 사용자의 안 읽은 메시지 목록 조회
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/unread")
    public ResponseEntity<BaseResponse<List<StompMessageLog>>> getUnreadMessages(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("call getUnreadMessages");
        List<StompMessageLog> unreadMessages = stompMessageLogService.getUnreadMessages(userDetails.getUsername());
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, unreadMessages));
    }
//
//    /**
//     * 특정 메시지 로그 삭제
//     */
//    @DeleteMapping("/logs/{logId}")
//    public void deleteStompLogById(@PathVariable Long logId) {
//        stompMessageLogService.deleteStompLogById(logId);
//    }
//

}

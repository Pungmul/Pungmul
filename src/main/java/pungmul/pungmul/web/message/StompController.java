package pungmul.pungmul.web.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.domain.message.domain.StompMessageReadStatus;
import pungmul.pungmul.dto.message.StompMessageDTO;
import pungmul.pungmul.service.message.MessageService;
import pungmul.pungmul.service.message.StompMessageLogService;
import pungmul.pungmul.service.message.StompMessageStatusService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompController {
    private final MessageService messageService;
    private final StompMessageLogService stompMessageLogService;
    private final StompMessageStatusService stompMessageStatusService;

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
//    @MessageMapping("/message")
//    public void markMessageAsRead(@RequestParam Long messageId, @RequestParam Long userId) {
//        stompMessageStatusService.markMessageAsRead(messageId, userId);
//    }
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
//    /**
//     * 특정 사용자의 안 읽은 메시지 목록 조회
//     */
//    @GetMapping("/unread")
//    public List<StompMessageLog> getUnreadMessages(@RequestParam Long userId) {
//        return stompMessageReadStatusService.getUnreadMessages(userId);
//    }
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

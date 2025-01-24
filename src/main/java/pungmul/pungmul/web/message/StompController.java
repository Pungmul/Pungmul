package pungmul.pungmul.web.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.dto.message.StompMessageDTO;
import pungmul.pungmul.service.message.MessageService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompController {
    private final MessageService messageService;

    @MessageMapping("/all")
    public void handleStompMessage(
            @Payload @Validated StompMessageDTO stompMessage,
//            @Header("Authorization") String authorizationToken,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ){
        log.info("Handling stomp message: {}", stompMessage);
        messageService.handleStompMessage(stompMessage, userDetails);
    }
}

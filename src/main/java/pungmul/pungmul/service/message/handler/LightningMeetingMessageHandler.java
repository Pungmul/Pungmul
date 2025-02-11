package pungmul.pungmul.service.message.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.message.StompMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class LightningMeetingMessageHandler implements StompMessageHandler{

    @Override
    public void handle(StompMessage message, UserDetailsImpl userDetails) {
        switch (message.getBusinessIdentifier()) {
            case "meeting-join":
                handleJoinMeeting(message.getContent(), userDetails);
                break;
            case "meeting-create":
                handleCreateMeeting(message.getContent(), userDetails);
                break;
            case "meeting-getNear":
                handleGetNearMeeting(message.getContent(), userDetails);
                break;
            default:
                throw new IllegalArgumentException("Unknown business identifier: " + message.getBusinessIdentifier());
        }
    }

    private void handleGetNearMeeting(Object content, UserDetailsImpl userDetails) {
//        lightningMeetingService.getNearLightningMeetings(content);
    }

    private void handleCreateMeeting(Object content, UserDetailsImpl userDetails) {
    }

    private void handleJoinMeeting(Object content, UserDetailsImpl userDetails) {

    }
}

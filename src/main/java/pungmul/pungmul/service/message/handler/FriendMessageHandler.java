package pungmul.pungmul.service.message.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.message.StompMessage;
import pungmul.pungmul.service.friend.FriendService;

@RequiredArgsConstructor
@Component
public class FriendMessageHandler implements StompMessageHandler {


    @Override
    public void handle(StompMessage message, UserDetailsImpl userDetails) {
        switch (message.getBusinessIdentifier()) {
            case "friend-request":
                handleFriendRequest(message.getContent(), userDetails);
                break;
            case "friend-accept":
                handleFriendAccept(message.getContent(), userDetails);
                break;
            default:
                throw new IllegalArgumentException("Unknown business identifier: " + message.getBusinessIdentifier());
        }
    }

    private void handleFriendAccept(Object content, UserDetailsImpl userDetails) {
    }

    private void handleFriendRequest(Object content, UserDetailsImpl userDetails) {

    }
}

package pungmul.pungmul.service.lightning;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.lightning.LightningMeeting;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LightningMeetingEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 번개 모임 생성 알림
     */
    public void notifyMeetingCreated(LightningMeeting meeting) {
        String destination = "/sub/nearby/updates";
        messagingTemplate.convertAndSend(destination, meeting);
    }

    /**
     * 번개 모임 삭제 알림
     */
    public void notifyMeetingDeleted(Long meetingId) {
        String destination = "/sub/nearby/updates";
        messagingTemplate.convertAndSend(destination, Map.of("deletedMeetingId", meetingId));
    }
}

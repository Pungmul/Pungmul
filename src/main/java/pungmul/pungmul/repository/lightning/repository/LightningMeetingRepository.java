package pungmul.pungmul.repository.lightning.repository;

import pungmul.pungmul.domain.lightning.LightningMeeting;
import pungmul.pungmul.domain.lightning.LightningMeetingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LightningMeetingRepository {
    void createLightningMeeting(LightningMeeting lightningMeeting);

    List<LightningMeeting> getAllLightningMeeting();

    List<LightningMeeting> findAllByDeadlineAndStatus(LocalDateTime now, LightningMeetingStatus status);

    Optional<LightningMeeting> getMeetingById(Long meetingId);

    void setStatus(Long meetingId, LightningMeetingStatus lightningMeetingStatus);
}

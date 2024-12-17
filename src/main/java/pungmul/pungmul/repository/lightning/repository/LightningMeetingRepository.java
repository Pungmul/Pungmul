package pungmul.pungmul.repository.lightning.repository;

import pungmul.pungmul.domain.lightning.LightningMeeting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LightningMeetingRepository {
    void createLightningMeeting(LightningMeeting lightningMeeting);

    List<LightningMeeting> getAllLightningMeeting();

    List<LightningMeeting> findAllByDeadline(LocalDateTime now);

    Optional<LightningMeeting> getMeetingById(Long meetingId);
}

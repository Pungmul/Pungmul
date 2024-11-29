package pungmul.pungmul.repository.lightning.repository;

import pungmul.pungmul.domain.lightning.LightningMeeting;

import java.time.LocalDateTime;
import java.util.List;

public interface LightningMeetingRepository {
    void createLightningMeeting(LightningMeeting lightningMeeting);

    List<LightningMeeting> getAllLightningMeeting();

    List<LightningMeeting> findAllByDeadline(LocalDateTime now);

}

package pungmul.pungmul.repository.lightning.repository;

import pungmul.pungmul.domain.lightning.LightningMeeting;

import java.util.List;

public interface LightningMeetingRepository {
    void createLightningMeeting(LightningMeeting lightningMeeting);

    List<LightningMeeting> getAllLightningMeeting();
}

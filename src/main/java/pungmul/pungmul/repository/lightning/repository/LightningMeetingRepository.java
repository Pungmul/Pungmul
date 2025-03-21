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

    List<LightningMeeting> findAllMeetingWithEnoughParticipants(LocalDateTime now, LightningMeetingStatus lightningMeetingStatus);

    List<LightningMeeting> findMeetingsStartingInThirtyMinutes(LocalDateTime now, LocalDateTime thirtyMinutesLater);

    void deactivateLightningMeeting(Long meetingId);

    void changeMeetingOrganizer(Long meetingId, Long userId);

    List<LightningMeeting> findSuccessfulMeetingsWithoutNotification(LocalDateTime now);

    void markNotificationAsSent(Long meetingId);

    List<LightningMeeting> findUnsuccessfulMeetingsPastStartTime(LocalDateTime now);

    void cancelMeetingsPastStartTime(LocalDateTime now);

    List<LightningMeeting> findMeetingsPastEndTime(LocalDateTime now);
}



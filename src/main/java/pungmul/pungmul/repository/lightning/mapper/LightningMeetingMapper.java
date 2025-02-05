package pungmul.pungmul.repository.lightning.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import pungmul.pungmul.domain.lightning.LightningMeeting;
import pungmul.pungmul.domain.lightning.LightningMeetingStatus;
import pungmul.pungmul.dto.lightning.SetMeetingStatusDTO;
import pungmul.pungmul.dto.lightning.TimeAndStatusDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface LightningMeetingMapper {
    void createLightningMeeting(LightningMeeting lightningMeeting);

    List<LightningMeeting> getAllLightningMeeting();

    List<LightningMeeting> findAllByDeadlineAndStatus(TimeAndStatusDTO timeAndStatus);

    Optional<LightningMeeting> getMeetingById(Long meetingId);

    void addMeetingParticipantNum(Long meetingId);

    void setStatus(SetMeetingStatusDTO meetingStatusDTO);

    List<LightningMeeting> findAllMeetingWithEnoughParticipants(TimeAndStatusDTO timeAndStatusDTO);

    List<LightningMeeting> findMeetingsStartingInThirtyMinutes(LocalDateTime now, LocalDateTime thirtyMinutesLater);

    void deactivateLightningMeeting(Long meetingId);

    void changeMeetingOrganizer(@Param("meetingId")Long meetingId, @Param("userId") Long userId);

    void markNotificationAsSent(Long meetingId);

    List<LightningMeeting> findSuccessfulMeetingsWithoutNotification(LocalDateTime now);

    List<LightningMeeting> findUnsuccessfulMeetingsPastStartTime(LocalDateTime now);

    void cancelMeetingsPastStartTime(LocalDateTime now);
}

package pungmul.pungmul.repository.lightning.mapper;

import org.apache.ibatis.annotations.Mapper;
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
}

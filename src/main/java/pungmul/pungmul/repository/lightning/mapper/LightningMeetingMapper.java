package pungmul.pungmul.repository.lightning.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.lightning.LightningMeeting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface LightningMeetingMapper {
    void createLightningMeeting(LightningMeeting lightningMeeting);

    List<LightningMeeting> getAllLightningMeeting();

    List<LightningMeeting> findAllByDeadline(LocalDateTime now);

    Optional<LightningMeeting> getMeetingById(Long meetingId);
}

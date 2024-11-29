package pungmul.pungmul.repository.lightning.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.lightning.LightningMeeting;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LightningMeetingMapper {
    void createLightningMeeting(LightningMeeting lightningMeeting);

    List<LightningMeeting> getAllLightningMeeting();

    List<LightningMeeting> findAllByDeadline(LocalDateTime now);
}

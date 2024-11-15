package pungmul.pungmul.repository.lightning.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.lightning.LightningMeetingParticipant;

@Mapper
public interface LightningMeetingParticipantMapper {
    void addLightningMeetingParticipant(LightningMeetingParticipant lightningMeetingParticipant);
}

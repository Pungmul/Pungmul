package pungmul.pungmul.repository.meeting.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.meeting.MeetingParticipant;

@Mapper
public interface MeetingParticipantMapper {
    void insertMeetingParticipant(MeetingParticipant participant);
}

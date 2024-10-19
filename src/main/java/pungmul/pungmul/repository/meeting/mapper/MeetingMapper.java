package pungmul.pungmul.repository.meeting.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.meeting.Meeting;

@Mapper
public interface MeetingMapper {
    void createMeeting(Meeting meeting);

    boolean existsByMeetingName(String meetingName);
}

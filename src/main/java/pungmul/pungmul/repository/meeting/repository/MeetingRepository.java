package pungmul.pungmul.repository.meeting.repository;

import pungmul.pungmul.domain.meeting.Meeting;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.dto.meeting.CreateMeetingRequestDTO;

public interface MeetingRepository {
    void createMeeting(Meeting meeting);

    boolean existsByMeetingName(String meetingName);

    Meeting getMeetingByMeetingId(Long meetingId);
}

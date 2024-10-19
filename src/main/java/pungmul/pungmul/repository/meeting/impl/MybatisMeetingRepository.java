package pungmul.pungmul.repository.meeting.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.meeting.Meeting;
import pungmul.pungmul.domain.meeting.MeetingStatus;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.dto.meeting.CreateMeetingRequestDTO;
import pungmul.pungmul.repository.meeting.mapper.MeetingMapper;
import pungmul.pungmul.repository.meeting.repository.MeetingRepository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MybatisMeetingRepository implements MeetingRepository {
    private final MeetingMapper meetingMapper;

    @Override
    public void createMeeting(Meeting meeting) {
        meetingMapper.createMeeting(meeting);
    }

    @Override
    public boolean existsByMeetingName(String meetingName) {
        return meetingMapper.existsByMeetingName(meetingName);
    }
}

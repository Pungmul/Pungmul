package pungmul.pungmul.repository.meeting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.meeting.MeetingParticipant;
import pungmul.pungmul.repository.meeting.mapper.MeetingParticipantMapper;
import pungmul.pungmul.repository.meeting.repository.MeetingParticipantRepository;

@RequiredArgsConstructor
@Repository
public class MybatisMeetingParticipantRepository implements MeetingParticipantRepository {
    private final MeetingParticipantMapper mapper;

    @Override
    public void save(MeetingParticipant participant) {
        mapper.insertMeetingParticipant(participant);
    }
}

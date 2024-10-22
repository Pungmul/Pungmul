package pungmul.pungmul.repository.meeting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.meeting.InvitationStatus;
import pungmul.pungmul.domain.meeting.MeetingInvitation;
import pungmul.pungmul.repository.meeting.mapper.MeetingInvitationMapper;
import pungmul.pungmul.repository.meeting.repository.MeetingInvitationRepository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class MybatisMeetingInvitationRepository implements MeetingInvitationRepository {
    private final MeetingInvitationMapper meetingInvitationMapper;

    @Override
    public void createMeetingInvitation(MeetingInvitation meetingInvitation) {
        meetingInvitationMapper.createMeetingInvitation(meetingInvitation);
    }

    @Override
    public List<MeetingInvitation> getInvitationsByReceiverId(Long receiverId) {
        return meetingInvitationMapper.getInvitationsByReceiverId(receiverId);
    }

    @Override
    public void updateInvitationStatus(Long invitationId, InvitationStatus status) {
        meetingInvitationMapper.updateInvitationStatus(invitationId, status);
    }
}

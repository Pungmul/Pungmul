package pungmul.pungmul.repository.meeting.repository;

import pungmul.pungmul.domain.meeting.InvitationStatus;
import pungmul.pungmul.domain.meeting.MeetingInvitation;

import java.util.List;

public interface MeetingInvitationRepository {
    void createMeetingInvitation(MeetingInvitation meetingInvitation);

    List<MeetingInvitation> getInvitationsByReceiverId(Long receiverId);

    void updateInvitationStatus(Long invitationId, InvitationStatus status);
}

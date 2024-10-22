package pungmul.pungmul.repository.meeting.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.meeting.InvitationStatus;
import pungmul.pungmul.domain.meeting.MeetingInvitation;

import java.util.List;

@Mapper
public interface MeetingInvitationMapper {
    void createMeetingInvitation(MeetingInvitation meetingInvitation);

    List<MeetingInvitation> getInvitationsByReceiverId(Long receiverId);

    void updateInvitationStatus(Long invitationId, InvitationStatus status);
    }

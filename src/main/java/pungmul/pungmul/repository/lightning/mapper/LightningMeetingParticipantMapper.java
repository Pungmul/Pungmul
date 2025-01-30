package pungmul.pungmul.repository.lightning.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import pungmul.pungmul.core.geo.LatLong;
import pungmul.pungmul.domain.lightning.LightningMeetingParticipant;
import pungmul.pungmul.domain.meeting.MeetingParticipant;

import java.util.Collection;
import java.util.List;

@Mapper
public interface LightningMeetingParticipantMapper {
    void addLightningMeetingParticipant(LightningMeetingParticipant lightningMeetingParticipant);

    List<LatLong> getMeetingParticipants(Long meetingId);

    Integer getMeetingParticipantNum(Long meetingId);

    void inactivateMeetingParticipants(Long meetingId);

    List<LightningMeetingParticipant> findAllParticipantsByMeetingId(Long meetingId);

    boolean isUserAlreadyParticipant(@Param("meetingId") Long meetingId,@Param("userId") Long id);

    boolean isUserInActiveMeeting(Long userId);

    void withdrawLightningMeeting(@Param("lightningMeetingId") Long lightningMeetingId, @Param("userId") Long id);

    boolean isUserParticipant(@Param("lightningMeetingId") Long lightningMeetingId,
                              @Param("userId") Long userId);

}

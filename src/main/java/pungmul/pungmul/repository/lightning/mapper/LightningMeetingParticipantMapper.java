package pungmul.pungmul.repository.lightning.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import pungmul.pungmul.core.geo.LatLong;
import pungmul.pungmul.domain.lightning.LightningMeetingParticipant;

import java.util.List;

@Mapper
public interface LightningMeetingParticipantMapper {
    void addLightningMeetingParticipant(LightningMeetingParticipant lightningMeetingParticipant);

    List<LatLong> getMeetingParticipants(Long meetingId);
}

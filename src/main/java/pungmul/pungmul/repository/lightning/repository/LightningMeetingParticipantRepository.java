package pungmul.pungmul.repository.lightning.repository;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import pungmul.pungmul.core.geo.LatLong;
import pungmul.pungmul.domain.lightning.LightningMeetingParticipant;

import java.util.List;

public interface LightningMeetingParticipantRepository {
    void addLightningMeetingParticipant(LightningMeetingParticipant lightningMeetingParticipant);

    List<LatLong> getMeetingParticipants(Long meetingId);
}

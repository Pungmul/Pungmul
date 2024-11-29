package pungmul.pungmul.repository.lightning.repository;

import pungmul.pungmul.core.geo.LatLong;
import pungmul.pungmul.domain.lightning.LightningMeetingParticipant;

import java.util.List;

public interface LightningMeetingParticipantRepository {
    void addLightningMeetingParticipant(LightningMeetingParticipant lightningMeetingParticipant);

    List<LatLong> getMeetingParticipantLocations(Long meetingId);

    Integer getMeetingParticipantNum(Long meetingId);

    void inactivateMeetingParticipants(Long meetingId);
}

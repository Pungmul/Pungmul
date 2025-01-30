package pungmul.pungmul.repository.lightning.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.core.geo.LatLong;
import pungmul.pungmul.domain.lightning.LightningMeetingParticipant;
import pungmul.pungmul.domain.meeting.MeetingParticipant;
import pungmul.pungmul.repository.lightning.mapper.LightningMeetingParticipantMapper;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingParticipantRepository;

import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MybatisLightningMeetingParticipantRepository implements LightningMeetingParticipantRepository {
    private final LightningMeetingParticipantMapper lightningMeetingParticipantMapper;
    @Override
    public void addLightningMeetingParticipant(LightningMeetingParticipant lightningMeetingParticipant) {
        log.info("lat : {}, log : {}", lightningMeetingParticipant.getLocation().getLatitude(), lightningMeetingParticipant.getLocation().getLongitude());
        lightningMeetingParticipantMapper.addLightningMeetingParticipant(lightningMeetingParticipant);
    }

    @Override
    public List<LatLong> getMeetingParticipantLocations(Long meetingId) {
        return lightningMeetingParticipantMapper.getMeetingParticipants(meetingId);
    }

    @Override
    public Integer getMeetingParticipantNum(Long meetingId) {
        return lightningMeetingParticipantMapper.getMeetingParticipantNum(meetingId);
    }

    @Override
    public void inactivateMeetingParticipants(Long meetingId) {
        lightningMeetingParticipantMapper.inactivateMeetingParticipants(meetingId);
    }

    @Override
    public List<LightningMeetingParticipant> findAllParticipantsByMeetingId(Long meetingId) {
        return lightningMeetingParticipantMapper.findAllParticipantsByMeetingId(meetingId);
    }

    @Override
    public boolean isUserAlreadyParticipant(Long meetingId, Long id) {
        return lightningMeetingParticipantMapper.isUserAlreadyParticipant(meetingId, id);
    }

    @Override
    public boolean isUserInActiveMeeting(Long userId) {
        return lightningMeetingParticipantMapper.isUserInActiveMeeting(userId);
    }

    @Override
    public void withdrawLightningMeeting(Long lightningMeetingId, Long id) {
        lightningMeetingParticipantMapper.withdrawLightningMeeting(lightningMeetingId, id);
    }

    @Override
    public boolean isUserParticipant(Long lightningMeetingId, Long id) {
        return lightningMeetingParticipantMapper.isUserParticipant(lightningMeetingId, id);
    }


}

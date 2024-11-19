package pungmul.pungmul.repository.lightning.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.core.geo.LatLong;
import pungmul.pungmul.domain.lightning.LightningMeetingParticipant;
import pungmul.pungmul.repository.lightning.mapper.LightningMeetingParticipantMapper;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingParticipantRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MybatisLightningMeetingParticipantRepository implements LightningMeetingParticipantRepository {
    private final LightningMeetingParticipantMapper lightningMeetingParticipantMapper;
    @Override
    public void addLightningMeetingParticipant(LightningMeetingParticipant lightningMeetingParticipant) {
        lightningMeetingParticipantMapper.addLightningMeetingParticipant(lightningMeetingParticipant);
    }

    @Override
    public List<LatLong> getMeetingParticipants(Long meetingId) {
        return lightningMeetingParticipantMapper.getMeetingParticipants(meetingId);
    }
}

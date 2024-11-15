package pungmul.pungmul.repository.lightning.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.lightning.LightningMeetingParticipant;
import pungmul.pungmul.repository.lightning.mapper.LightningMeetingParticipantMapper;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingParticipantRepository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MybatisLightningMeetingParticipantRepository implements LightningMeetingParticipantRepository {
    private final LightningMeetingParticipantMapper lightningMeetingParticipantMapper;
    @Override
    public void addLightningMeetingParticipant(LightningMeetingParticipant lightningMeetingParticipant) {
        lightningMeetingParticipantMapper.addLightningMeetingParticipant(lightningMeetingParticipant);
    }
}

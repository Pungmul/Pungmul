package pungmul.pungmul.repository.lightning.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.lightning.LightningMeeting;
import pungmul.pungmul.repository.lightning.mapper.LightningMeetingMapper;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MybatisLightningMeetingRepository implements LightningMeetingRepository {
    private final LightningMeetingMapper lightningMeetingMapper;

    @Override
    public void createLightningMeeting(LightningMeeting lightningMeeting) {
        lightningMeetingMapper.createLightningMeeting(lightningMeeting);
    }

    @Override
    public List<LightningMeeting> getAllLightningMeeting() {
        return lightningMeetingMapper.getAllLightningMeeting();
    }
}

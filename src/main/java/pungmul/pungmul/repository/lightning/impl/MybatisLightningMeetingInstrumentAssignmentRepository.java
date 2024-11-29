package pungmul.pungmul.repository.lightning.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.lightning.InstrumentAssignment;
import pungmul.pungmul.domain.member.instrument.Instrument;
import pungmul.pungmul.repository.lightning.mapper.LightningMeetingInstrumentAssignmentMapper;
import pungmul.pungmul.repository.lightning.repository.LightningMeetingInstrumentAssignmentRepository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MybatisLightningMeetingInstrumentAssignmentRepository implements LightningMeetingInstrumentAssignmentRepository {
    private final LightningMeetingInstrumentAssignmentMapper lightningMeetingInstrumentAssignmentMapper;


    @Override
    public void increaseAssignment(Long meetingId, Instrument instrument) {
        lightningMeetingInstrumentAssignmentMapper.increaseAssignment(meetingId, instrument);
    }

    @Override
    public void createAssignment(InstrumentAssignment instrumentAssignment) {
        lightningMeetingInstrumentAssignmentMapper.createAssignment(instrumentAssignment);
    }

    @Override
    public Integer getCurrentInstrumentAssign(Instrument instrument) {
        return lightningMeetingInstrumentAssignmentMapper.getCurrentInstrumentAssign(instrument);
    }
}

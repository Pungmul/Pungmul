package pungmul.pungmul.repository.lightning.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.lightning.InstrumentAssignment;
import pungmul.pungmul.domain.member.instrument.Instrument;

@Mapper
public interface LightningMeetingInstrumentAssignmentMapper {
    void createAssignment(InstrumentAssignment instrumentAssignment);

    void increaseAssignment(Long meetingId, Instrument instrument);

    void decreaseAssignment(Long meetingId, Instrument instrument);

    Integer getCurrentInstrumentAssign(Instrument instrument);
}

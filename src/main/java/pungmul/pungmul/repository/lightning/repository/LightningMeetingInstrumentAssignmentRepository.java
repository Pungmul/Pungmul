package pungmul.pungmul.repository.lightning.repository;

import pungmul.pungmul.domain.lightning.InstrumentAssignment;
import pungmul.pungmul.domain.member.instrument.Instrument;

public interface LightningMeetingInstrumentAssignmentRepository {
    void increaseAssignment(Long meetingId, Instrument instrument);

    void createAssignment(InstrumentAssignment instrumentAssignment);
}

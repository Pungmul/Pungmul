package pungmul.pungmul.repository.member.base.repository;

import pungmul.pungmul.domain.member.Instrument;
import pungmul.pungmul.domain.member.InstrumentStatus;

import java.util.List;

public interface InstrumentStatusRepository {

    void saveInstrument(InstrumentStatus instrument);

    public List<InstrumentStatus> getAllInstrumentStatusByUserId(Long userId);

    public InstrumentStatus getInstrumentStatusByInstrumentId(Long instrumentId);

    public InstrumentStatus getInstrumentStatusByUserIdAndInstrumentType(Long userId, Instrument instrument);

}

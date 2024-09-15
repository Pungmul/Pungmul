package pungmul.pungmul.repository.member.repository;

import pungmul.pungmul.domain.member.instrument.Instrument;
import pungmul.pungmul.domain.member.instrument.InstrumentStatus;

import java.util.List;
import java.util.Optional;

public interface InstrumentStatusRepository {

    void saveInstrument(InstrumentStatus instrument);

    public Optional<List<InstrumentStatus>> getAllInstrumentStatusByUserId(Long userId);

    public Optional<InstrumentStatus> getInstrumentStatusByInstrumentId(Long instrumentId);

    public Optional<InstrumentStatus> getInstrumentStatusByUserIdAndInstrumentType(Long userId, Instrument instrument);

}

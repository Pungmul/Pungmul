package pungmul.pungmul.repository.member.base.impl;

import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.Instrument;
import pungmul.pungmul.domain.member.InstrumentStatus;
import pungmul.pungmul.repository.member.base.mapper.InstrumentStatusMapper;
import pungmul.pungmul.repository.member.base.repository.InstrumentStatusRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class MybatisInstrumentStatusRepository implements InstrumentStatusRepository {
    private final InstrumentStatusMapper instrumentStatusMapper;

    public MybatisInstrumentStatusRepository(InstrumentStatusMapper instrumentStatusMapper){
        this.instrumentStatusMapper = instrumentStatusMapper;
    }

    @Override
    public void saveInstrument(InstrumentStatus instrument) {
        instrumentStatusMapper.saveInstrument(instrument);
    }

    @Override
    public Optional<List<InstrumentStatus>> getAllInstrumentStatusByUserId(Long userId) {
        return Optional.ofNullable(instrumentStatusMapper.getAllInstrumentStatusByUserId(userId));
    }

    @Override
    public Optional<InstrumentStatus> getInstrumentStatusByInstrumentId(Long instrumentStatusId) {
        return Optional.ofNullable(instrumentStatusMapper.getInstrumentStatusByInstrumentId(instrumentStatusId));
    }

    @Override
    public Optional<InstrumentStatus> getInstrumentStatusByUserIdAndInstrumentType(Long userId, Instrument instrument) {
        return Optional.ofNullable(instrumentStatusMapper.getInstrumentStatusByUserIdAndInstrumentType(userId, instrument));
    }
}

package pungmul.pungmul.repository.member.base.impl;

import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.Instrument;
import pungmul.pungmul.domain.member.InstrumentStatus;
import pungmul.pungmul.repository.member.base.mapper.InstrumentStatusMapper;
import pungmul.pungmul.repository.member.base.repository.InstrumentStatusRepository;

import java.util.List;

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
    public List<InstrumentStatus> getAllInstrumentStatusByUserId(Long userId) {
        return instrumentStatusMapper.getAllInstrumentStatusByUserId(userId);
    }

    @Override
    public InstrumentStatus getInstrumentStatusByInstrumentId(Long instrumentStatusId) {
        return instrumentStatusMapper.getInstrumentStatusByInstrumentId(instrumentStatusId);
    }

    @Override
    public InstrumentStatus getInstrumentStatusByUserIdAndInstrumentType(Long userId, Instrument instrument) {
        return instrumentStatusMapper.getInstrumentStatusByUserIdAndInstrumentType(userId, instrument);
    }
}

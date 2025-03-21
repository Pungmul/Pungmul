package pungmul.pungmul.repository.member.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.instrument.Instrument;
import pungmul.pungmul.domain.member.instrument.InstrumentStatus;
import pungmul.pungmul.repository.member.mapper.InstrumentStatusMapper;
import pungmul.pungmul.repository.member.repository.InstrumentStatusRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MybatisInstrumentStatusRepository implements InstrumentStatusRepository {
    private final InstrumentStatusMapper instrumentStatusMapper;

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

    @Override
    public void updateInstrumentStatus(InstrumentStatus instrumentStatus) {
        instrumentStatusMapper.updateInstrumentStatus(instrumentStatus);
    }

    @Override
    public void setMajorFalseForOtherInstruments(InstrumentStatus instrumentStatus) {
        instrumentStatusMapper.setMajorFalseForOtherInstruments(instrumentStatus);
    }

    @Override
    public void updateInstrumentAbilityAndMajor(InstrumentStatus instrumentStatus) {
        instrumentStatusMapper.updateInstrumentAbilityAndMajor(instrumentStatus);
    }

    @Override
    public Instrument getMajorInstrumentByUserId(Long userId) {
        return instrumentStatusMapper.getMajorInstrumentByUserId(userId);
    }

    @Override
    public List<Instrument> getAllInstruments(Long id) {
        return instrumentStatusMapper.getAllInstruments(id);
    }
}

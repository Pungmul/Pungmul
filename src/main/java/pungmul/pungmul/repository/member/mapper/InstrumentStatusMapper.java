package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.member.instrument.Instrument;
import pungmul.pungmul.domain.member.instrument.InstrumentStatus;

import java.util.List;

@Mapper
public interface InstrumentStatusMapper {
    void saveInstrument(InstrumentStatus instrumentStatus);

    List<InstrumentStatus> getAllInstrumentStatusByUserId(Long userId);

    InstrumentStatus getInstrumentStatusByInstrumentId(Long id);

    InstrumentStatus getInstrumentStatusByUserIdAndInstrumentType(Long userId, Instrument instrument);

    void updateInstrumentStatus(InstrumentStatus instrumentStatus);

    void setMajorFalseForOtherInstruments(InstrumentStatus instrumentStatus);

    void updateInstrumentAbilityAndMajor(InstrumentStatus instrumentStatus);
}

package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.member.Instrument;
import pungmul.pungmul.domain.member.InstrumentStatus;

import java.util.List;

@Mapper
public interface InstrumentStatusMapper {
    void saveInstrument(InstrumentStatus instrumentStatus);

    List<InstrumentStatus> getAllInstrumentStatusByUserId(Long userId);

    InstrumentStatus getInstrumentStatusByInstrumentId(Long id);

    InstrumentStatus getInstrumentStatusByUserIdAndInstrumentType(Long userId, Instrument instrument);

}

package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.member.InstrumentStatus;

@Mapper
public interface InstrumentStatusMapper {
    void saveInstrument(InstrumentStatus instrumentStatus);
}

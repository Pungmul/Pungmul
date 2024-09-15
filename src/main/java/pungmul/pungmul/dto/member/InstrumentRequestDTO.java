package pungmul.pungmul.dto.member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.member.instrument.InstrumentStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstrumentRequestDTO {
    private Long userId;
    private List<InstrumentStatus> instrumentStatusList;
}

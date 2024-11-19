package pungmul.pungmul.dto.lightning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.member.instrument.Instrument;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstrumentAssignRequestDTO {
    private Instrument instrument;
    private Integer minPersonNum;
    private Integer maxPersonNum;
}

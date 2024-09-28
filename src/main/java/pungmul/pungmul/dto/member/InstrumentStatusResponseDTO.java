package pungmul.pungmul.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.member.instrument.Instrument;
import pungmul.pungmul.domain.member.instrument.InstrumentAbility;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstrumentStatusResponseDTO {
    private Instrument instrument;
    private InstrumentAbility instrumentAbility;
    private boolean major;
}

package pungmul.pungmul.domain.member.instrument;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InstrumentStatus {

    private Long id;
    private Long userId;
    private Instrument instrument;
    private InstrumentAbility instrumentAbility;
    private boolean major;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

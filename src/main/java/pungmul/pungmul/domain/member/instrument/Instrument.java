package pungmul.pungmul.domain.member.instrument;

import lombok.Getter;

@Getter
public enum Instrument {
    KKWAENGGWARI("꽹과리"),
    JING("징"),
    JANGGU("장구"),
    BUK("북"),
    SOGO("소고"),
    TAEPYUNGSO("태평소");

    private final String code;

    Instrument(String code) {
        this.code = code;
    }

}

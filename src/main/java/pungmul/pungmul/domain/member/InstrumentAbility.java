package pungmul.pungmul.domain.member;

public enum InstrumentAbility {

    UNSKILLED("초보"),
    BASIC("초급"),
    INTERMEDIATE("중급"),
    ADVANCED("고급"),
    EXPERT("전문가");

    private final String code;

    InstrumentAbility(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

package pungmul.pungmul.domain.member;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public enum Gender {
    @JsonProperty("M")
    MALE("M"),
    @JsonProperty("F")
    FEMALE("F");

    private final String value;

    Gender(final String value) {
        this.value = value;
    }

    public String getValue() {return value;}

    @Override
    public String toString() {return name();}
}

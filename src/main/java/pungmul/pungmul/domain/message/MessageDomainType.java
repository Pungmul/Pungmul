package pungmul.pungmul.domain.message;


import lombok.Getter;

@Getter
public enum MessageDomainType {
    FRIEND("friend"),
    MEETING("meeting"),
    USER("user"),
    SYSTEM("system"),
    NOTIFICATION("notification"),
    LIGHTNING_MEETING("lightning-meeting");

    private final String path;

    MessageDomainType(String path) {
        this.path = path;
    }
}

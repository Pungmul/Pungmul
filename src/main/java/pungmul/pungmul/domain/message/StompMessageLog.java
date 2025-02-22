package pungmul.pungmul.domain.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StompMessageLog {
    private Long id;
    private Long senderId;
    private MessageDomainType domainType;
    private String businessIdentifier;
    private String identifier;
    private String stompDest;
    private String content;
    private LocalDateTime sentAt;
}

package pungmul.pungmul.domain.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StompMessageResponse {
    private Long messageLogId;
    private MessageDomainType domainType;
    private String businessIdentifier;
    private String identifier;
    private String stompDest;
    private String content;
}

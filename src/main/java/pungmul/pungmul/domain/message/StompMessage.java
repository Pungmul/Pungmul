package pungmul.pungmul.domain.message;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.dto.message.StompMessageDTO;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StompMessage {
//    @NotNull
//    private MessageType messageType;
    @NotNull
    private MessageDomainType domainType;
    @NotNull
    private String businessIdentifier;
    private String identifier; // 특정 대상 식별자
    private Object content;    // 메시지 내용

    // 정적 팩토리 메서드로 변환
    public static StompMessage from(StompMessageDTO dto) {
        return StompMessage.builder()
                .domainType(dto.getDomainType())
                .businessIdentifier(dto.getBusinessIdentifier())
                .identifier(dto.getIdentifier())
                .content(dto.getContent())
                .build();
    }
}

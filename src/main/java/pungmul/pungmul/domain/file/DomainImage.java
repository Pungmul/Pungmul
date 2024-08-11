package pungmul.pungmul.domain.file;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DomainImage {
    private Long id;
    private String domainType;
    private Long domainId;
    private Long imageId;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

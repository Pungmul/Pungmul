package pungmul.pungmul.domain.file;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DomainImage {
    private Long id;
    private DomainType domainType;
    private Long domainId;
    private Long imageId;
    private Long userId;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

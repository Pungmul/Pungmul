package pungmul.pungmul.dto.file;

import lombok.*;
import pungmul.pungmul.domain.file.DomainType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DomainImageDTO {
    private Long id;
    private DomainType domainType;
    private Long domainId;
    private Long imageId;
    private Long userId;
//    private Boolean isPrimary;
}

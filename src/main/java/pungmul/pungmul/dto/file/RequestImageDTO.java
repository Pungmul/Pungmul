package pungmul.pungmul.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.file.DomainType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestImageDTO {
    private Long domainId;
    private Long userId;
    private DomainType domainType;
    private MultipartFile imageFile;
}

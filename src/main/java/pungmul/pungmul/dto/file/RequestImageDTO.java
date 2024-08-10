package pungmul.pungmul.dto.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestImageDTO {
    private Long userId;
    private MultipartFile imageFile;
}

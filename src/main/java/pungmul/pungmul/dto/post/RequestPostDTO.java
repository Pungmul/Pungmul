package pungmul.pungmul.dto.post;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestPostDTO {
//    private Long userId;
    private String title;
    private String text;
    private boolean anonymity;
    private Long categoryId;
}

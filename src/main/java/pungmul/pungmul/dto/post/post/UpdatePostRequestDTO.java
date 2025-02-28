package pungmul.pungmul.dto.post.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequestDTO {
    private String text;
    private boolean anonymity;
    private List<Long> deleteImageIdList;
}

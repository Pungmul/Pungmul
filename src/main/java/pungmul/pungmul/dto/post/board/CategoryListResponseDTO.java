package pungmul.pungmul.dto.post.board;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryListResponseDTO {
    private List<CategoryDTO> categoryDTOList;
}

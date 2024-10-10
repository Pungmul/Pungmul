package pungmul.pungmul.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pungmul.pungmul.domain.file.Image;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUserDTO {
    private Long userId;
    private String name;
    private Image profileImage;

}

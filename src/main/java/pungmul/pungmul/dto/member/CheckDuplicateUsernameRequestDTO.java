package pungmul.pungmul.dto.member;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckDuplicateUsernameRequestDTO {
    private String username;
}

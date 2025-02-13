package pungmul.pungmul.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckDuplicateUsernameResponseDTO {
    private Boolean isRegistered;   //  True이면 이미 존재하는 username, False이면 사용가능한 username
}

package pungmul.pungmul.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BanMemberRequestDTO {
    private String username;
    private String banReason;
    private LocalDateTime banUntil;
}

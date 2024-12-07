package pungmul.pungmul.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BanMemberResponseDTO {
    private String name;
    private LocalDateTime banUntil;
    private String banReason;
}

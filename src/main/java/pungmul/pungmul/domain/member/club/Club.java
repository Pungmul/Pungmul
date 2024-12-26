package pungmul.pungmul.domain.member.club;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Club {
    private Long id;
    private String name;
    private String school;
    private Long headId;
    private LocalDateTime cratedAt;
    private LocalDateTime updatedAt;
}

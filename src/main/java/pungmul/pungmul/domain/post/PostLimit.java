package pungmul.pungmul.domain.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostLimit {
    private Long id;
    private Long userId;
    private Integer postCount;
    private LocalDateTime lastResetTime;
}

package pungmul.pungmul.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberResponseDTO {
//    private String username;
    private String name;
    private String clubName;
    private String phoneNumber;
//    private Integer clubAge;
//    private String area;
    private Long clubId;
    private LocalDate updateAt;
}

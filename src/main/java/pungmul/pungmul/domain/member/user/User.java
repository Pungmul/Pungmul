package pungmul.pungmul.domain.member.user;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    private Long id;
    private Long accountId;
    private String name;
    private String clubName;
    private LocalDate birth;
    private String email;
    private String phoneNumber;
    private Integer clubAge;
    private Gender gender;
    private String area;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long clubId;
}

package pungmul.pungmul.dto.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InviteUserToMeetingResponseDTO {
    private List<InviteUser> inviteUsers = new ArrayList<>();
    private List<String> failedEmails = new ArrayList<>(); // 실패한 이메일 리스트

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InviteUser {
        private Long meetingId;
        private String email;
    }
}

package pungmul.pungmul.service.meeting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.core.exception.custom.meeting.MeetingNameAlreadyExistsException;
import pungmul.pungmul.domain.meeting.InvitationStatus;
import pungmul.pungmul.domain.meeting.Meeting;
import pungmul.pungmul.domain.meeting.MeetingInvitation;
import pungmul.pungmul.domain.meeting.MeetingStatus;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.dto.meeting.CreateMeetingRequestDTO;
import pungmul.pungmul.dto.meeting.CreateMeetingResponseDTO;
import pungmul.pungmul.dto.meeting.InviteUserToMeetingRequestDTO;
import pungmul.pungmul.repository.meeting.repository.MeetingInvitationRepository;
import pungmul.pungmul.repository.meeting.repository.MeetingRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingInvitationRepository meetingInvitationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public CreateMeetingResponseDTO createMeeting(UserDetails userDetails, CreateMeetingRequestDTO createMeetingRequestDTO) {

        if (meetingRepository.existsByMeetingName(createMeetingRequestDTO.getMeetingName())) {
            throw new MeetingNameAlreadyExistsException("이미 존재하는 미팅 이름입니다.");
        }

        Meeting meeting = getMeeting(userDetails, createMeetingRequestDTO);
        meetingRepository.createMeeting(meeting);
        return getCreateMeetingResponseDTO(meeting);
    }

    //  초대 메세지 전송 메소드. 로그인 사용자 정보와 메세지 전송 대상 이메일 리스트를 인자로 받음
    public void inviteUserToMeeting(UserDetails userDetails, InviteUserToMeetingRequestDTO inviteUserToMeetingRequestDTO) {
        List<String> inviteUserEmailList = inviteUserToMeetingRequestDTO.getInviteUserEmailList();
        Long meetingId = inviteUserToMeetingRequestDTO.getMeetingId();
        User founder = userRepository.getUserByEmail(userDetails.getUsername())
                .orElseThrow(NoSuchElementException::new);

        for (String email : inviteUserEmailList) {
            User receiver = userRepository.getUserByEmail(email)
                    .orElseThrow(NoSuchElementException::new);

            meetingInvitationRepository.createMeetingInvitation(getMeetingInvitation(meetingId, founder, receiver));

            sendInvitation(receiver, founder);
        }
    }

    private CreateMeetingResponseDTO getCreateMeetingResponseDTO(Meeting meeting) {
//        String founderName = userRepository.getUserByUserId(meeting.getFounderUserId())
//                .map(user -> Optional.ofNullable(user.getClubName()).orElse(user.getName()))  // clubName이 null일 경우 name 반환
//                .orElseThrow(NoSuchElementException::new);

                String founderName = userRepository.getUserByUserId(meeting.getFounderUserId())
                .map(User::getName)
                .orElseThrow(NoSuchElementException::new);

        return CreateMeetingResponseDTO.builder()
                .meetingId(meeting.getId())
                .meetingName(meeting.getMeetingName())
                .meetingDescription(meeting.getMeetingDescription())
                .isPublic(meeting.getIsPublic())
                .meetingStatus(meeting.getMeetingStatus())
                .founderUserName(founderName)
//                .createdAt(meeting.getCreatedAt())
//                .updatedAt(meeting.getUpdatedAt())
                .build();
    }

    private Meeting getMeeting(UserDetails userDetails, CreateMeetingRequestDTO createMeetingRequestDTO) {
        User founder = userRepository.getUserByEmail(userDetails.getUsername())
                .orElseThrow(NoSuchElementException::new);

        return Meeting.builder()
                .meetingName(createMeetingRequestDTO.getMeetingName())
                .meetingDescription(createMeetingRequestDTO.getMeetingDescription())
                .isPublic(createMeetingRequestDTO.getIsPublic())
                .meetingStatus(MeetingStatus.CREATED)
                .founderUserId(founder.getId())
                .build();
    }

    private void sendInvitation(User receiver, User founder) {
        String dest = "/sub/meeting/" + receiver.getEmail();
        // 메시지 내용 (초대 메시지의 내용을 정의)

        String message = founder.getName() + "님이 모임에 초대합니다.";

        // convertAndSendToUser를 사용하여 각 사용자에게 메시지 전송
        messagingTemplate.convertAndSend(dest, message);
    }

    private static MeetingInvitation getMeetingInvitation(Long meetingId, User founder, User receiver) {
        return MeetingInvitation.builder()
                .meetingId(meetingId)
                .founderId(founder.getId())
                .receiverId(receiver.getId())
                .invitationStatus(InvitationStatus.PENDING)
                .build();
    }
}






























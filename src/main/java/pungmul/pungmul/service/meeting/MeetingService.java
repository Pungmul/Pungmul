package pungmul.pungmul.service.meeting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.core.exception.custom.meeting.MeetingNameAlreadyExistsException;
import pungmul.pungmul.domain.meeting.*;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.dto.meeting.*;
import pungmul.pungmul.repository.meeting.repository.MeetingInvitationRepository;
import pungmul.pungmul.repository.meeting.repository.MeetingParticipantRepository;
import pungmul.pungmul.repository.meeting.repository.MeetingRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingInvitationRepository meetingInvitationRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public CreateMeetingResponseDTO createMeeting(UserDetails userDetails, CreateMeetingRequestDTO createMeetingRequestDTO) {

        if (meetingRepository.existsByMeetingName(createMeetingRequestDTO.getMeetingName())) {
            throw new MeetingNameAlreadyExistsException("이미 존재하는 미팅 이름입니다.");
        }

        Meeting meeting = getMeeting(userDetails, createMeetingRequestDTO);
        meetingRepository.createMeeting(meeting);

        MeetingParticipant founderParticipant = MeetingParticipant.builder()
                .meetingId(meeting.getId())
                .userId(userRepository.getUserByEmail(userDetails.getUsername()).map(User::getId).orElseThrow(NoSuchElementException::new))
                .joinedAt(LocalDate.now())
                .isHost(Boolean.TRUE)
                .build();

        meetingParticipantRepository.save(founderParticipant);
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

            MeetingInvitation meetingInvitation = getMeetingInvitation(meetingId, founder, receiver);
            meetingInvitationRepository.createMeetingInvitation(meetingInvitation);

            sendInvitation(receiver, founder, meetingId, meetingInvitation.getId());
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

    private void sendInvitation(User receiver, User founder, Long meetingId, Long invitationId) {
        //  전송 경로
        String dest = "/sub/meeting/" + receiver.getEmail();

        // 메시지 내용 (초대 메시지의 내용을 정의)
        MeetingInvitationMessageDTO invitationMessage = getInvitationMessage(founder, meetingId, invitationId);

        // convertAndSend를 사용하여 각 사용자에게 메시지 전송
        messagingTemplate.convertAndSend(dest, invitationMessage);
    }

    private MeetingInvitationMessageDTO getInvitationMessage(User founder, Long meetingId, Long invitationId) {
        Meeting meeting = meetingRepository.getMeetingByMeetingId(meetingId);
        return MeetingInvitationMessageDTO.builder()
                .meetingId(meetingId)
                .invitationId(invitationId)
                .meetingName(meeting.getMeetingName())
                .founderId(founder.getId())
                .founderName(founder.getName())
                .message(founder.getName() + "님이 모임에 초대합니다.")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private MeetingInvitation getMeetingInvitation(Long meetingId, User founder, User receiver) {
        return MeetingInvitation.builder()
                .meetingId(meetingId)
                .founderId(founder.getId())
                .receiverId(receiver.getId())
                .invitationStatus(InvitationStatus.PENDING)
                .build();
    }

    public MeetingInvitationReplyResponseDTO replyInvitation(UserDetails userDetails, MeetingInvitationReplyRequestDTO replyRequest) {
        // 초대 정보 조회
        MeetingInvitation invitation = meetingInvitationRepository
                .getInvitationById(replyRequest.getInvitationId())
                .orElseThrow(() -> new NoSuchElementException("해당 초대가 존재하지 않습니다."));

        Long userId = userRepository.getUserByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(NoSuchElementException::new);

        // 초대 상태 업데이트
        meetingInvitationRepository.updateInvitationStatus(invitation.getId(), replyRequest.getInvitationStatus());

        // 초대가 ACCEPTED 상태인 경우 모임 참가자 테이블에 추가
        if (replyRequest.getInvitationStatus() == InvitationStatus.ACCEPTED) {
            // 참가자 정보 저장
            MeetingParticipant participant = getMeetingParticipant(replyRequest, userId);
            meetingParticipantRepository.save(participant);
        }

        // 응답 데이터 작성
        return getMeetingInvitationReplyResponseDTO(replyRequest, invitation);
    }

    private static MeetingParticipant getMeetingParticipant(MeetingInvitationReplyRequestDTO replyRequest, Long userId) {
        return MeetingParticipant.builder()
                .meetingId(replyRequest.getMeetingId())
                .userId(userId)
                .joinedAt(LocalDate.now())
                .isHost(Boolean.FALSE)
                .build();
    }

    private static MeetingInvitationReplyResponseDTO getMeetingInvitationReplyResponseDTO(MeetingInvitationReplyRequestDTO replyRequest, MeetingInvitation invitation) {
        return MeetingInvitationReplyResponseDTO.builder()
                .meetingId(invitation.getMeetingId())
                .invitationStatus(replyRequest.getInvitationStatus())
                .message("초대에 대한 응답이 " + replyRequest.getInvitationStatus().getDescription() + " 처리되었습니다.")
                .build();
    }
}






























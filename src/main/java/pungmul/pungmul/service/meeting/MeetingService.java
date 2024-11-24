package pungmul.pungmul.service.meeting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.core.exception.custom.meeting.MeetingNameAlreadyExistsException;
import pungmul.pungmul.domain.friend.Friend;
import pungmul.pungmul.domain.friend.FriendStatus;
import pungmul.pungmul.domain.meeting.*;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.domain.message.MessageType;
import pungmul.pungmul.dto.friend.FriendRequestDTO;
import pungmul.pungmul.dto.meeting.*;
import pungmul.pungmul.dto.member.SimpleUserDTO;
import pungmul.pungmul.repository.friend.repository.FriendRepository;
import pungmul.pungmul.repository.meeting.repository.MeetingInvitationRepository;
import pungmul.pungmul.repository.meeting.repository.MeetingParticipantRepository;
import pungmul.pungmul.repository.meeting.repository.MeetingRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.service.friend.FriendService;
import pungmul.pungmul.service.message.MessageService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final FriendRepository friendRepository;
    private final FriendService friendService;
    private final MessageService messageService;

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
    public InviteUserToMeetingResponseDTO inviteUserToMeeting(UserDetails userDetails, InviteUserToMeetingRequestDTO inviteUserToMeetingRequestDTO) {
        List<String> inviteUserEmailList = inviteUserToMeetingRequestDTO.getInviteUserEmailList();
        Long meetingId = inviteUserToMeetingRequestDTO.getMeetingId();
        User founder = userRepository.getUserByEmail(userDetails.getUsername())
                .orElseThrow(NoSuchElementException::new);

        InviteUserToMeetingResponseDTO inviteUserToMeetingResponseDTO = new InviteUserToMeetingResponseDTO();

        for (String email : inviteUserEmailList) {
            try {
                User receiver = userRepository.getUserByEmail(email)
                        .orElseThrow(() -> new NoSuchElementException("User not found: " + email));

                MeetingInvitation meetingInvitation = getMeetingInvitation(meetingId, founder, receiver);
                meetingInvitationRepository.createMeetingInvitation(meetingInvitation);

                sendInvitationMessage(receiver, founder, meetingId, meetingInvitation.getId());

                inviteUserToMeetingResponseDTO.getInviteUsers()
                        .add(InviteUserToMeetingResponseDTO.InviteUser.builder()
                                .meetingId(meetingId)
                                .email(receiver.getEmail()).build());
            } catch (NoSuchElementException e) {
                log.warn("Failed to invite user with email: {}", email, e);
                inviteUserToMeetingResponseDTO.getFailedEmails().add(email);
            }
        }

        return inviteUserToMeetingResponseDTO;
    }
    // 새로운 메시지 전송 메서드
    private void sendInvitationMessage(User receiver, User founder, Long meetingId, Long invitationId) {
        // MeetingInvitationMessageDTO를 생성
        MeetingInvitationMessageDTO invitationMessage = getInvitationMessage(founder, meetingId, invitationId);

        // MessageService를 사용하여 메시지 전송
        messageService.sendMessage(
                MessageType.INVITATION,
                MessageDomainType.MEETING,
                receiver.getEmail(), // 수신자 identifier로 이메일 사용
                invitationMessage // 초대 메시지 내용
        );
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
                .content(founder.getName() + "님이 모임에 초대합니다.")
//                .sentAt(LocalDateTime.now())
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

    public FriendsToInviteResponseDTO getFriendsToInvite(UserDetails userDetails) {
        Long userId = userRepository.getUserByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(NoSuchElementException::new);

        List<SimpleUserDTO> friendList = friendRepository.getFriendList(userId).stream()
                .filter(friend -> friend.getStatus() == FriendStatus.ACCEPTED)
                // 양방향 관계에서 한쪽만 조회되도록 필터링
                .filter(friend -> friend.getSenderId() < friend.getReceiverId())
                .map(friend -> {
                    try {
                        return friendService.getFriendRequestDTO(userId, friend);
                    } catch (NoSuchElementException e) {
                        log.warn("조회할 수 없는 사용자: {}", friend.getId(), e);
                        return null; // 오류가 발생한 데이터는 제외
                    }
                })
                .filter(Objects::nonNull) // null인 데이터를 제외
                .map(FriendRequestDTO::getSimpleUserDTO)
                .collect(Collectors.toList());

        // FriendsToInviteResponseDTO에 담아서 반환
        return FriendsToInviteResponseDTO.builder()
                .friendsToInvite(friendList)
                .build();
    }

        public void processInvitationMessage(String username, MeetingInvitationMessageDTO message) {
            log.info("Processing meeting invitation for user: {}", username);

            // MessageService를 통해 메시지를 전송
            messageService.sendMessage(MessageType.INVITATION, MessageDomainType.MEETING, username, message);

            log.info("Meeting invitation sent for user: {}", username);
        }
}




























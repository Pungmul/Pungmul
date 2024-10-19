package pungmul.pungmul.service.meeting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.meeting.Meeting;
import pungmul.pungmul.domain.meeting.MeetingStatus;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.dto.meeting.CreateMeetingRequestDTO;
import pungmul.pungmul.dto.meeting.CreateMeetingResponseDTO;
import pungmul.pungmul.repository.meeting.repository.MeetingRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;

    public CreateMeetingResponseDTO createMeeting(UserDetails userDetails, CreateMeetingRequestDTO createMeetingRequestDTO) {

        if (meetingRepository.existsByMeetingName(createMeetingRequestDTO.getMeetingName())) {
            throw new IllegalArgumentException("이미 존재하는 미팅 이름입니다.");
        }

        Meeting meeting = getMeeting(userDetails, createMeetingRequestDTO);
        meetingRepository.createMeeting(meeting);
        return getCreateMeetingResponseDTO(meeting);
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
}

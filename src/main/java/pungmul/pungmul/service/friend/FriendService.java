package pungmul.pungmul.service.friend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.friend.Friend;
import pungmul.pungmul.domain.friend.FriendRequestFrom;
import pungmul.pungmul.domain.friend.FriendStatus;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.message.MessageDomainType;
import pungmul.pungmul.domain.message.MessageType;
import pungmul.pungmul.dto.friend.AvailableFriendDTO;
import pungmul.pungmul.dto.friend.FriendListResponseDTO;
import pungmul.pungmul.dto.friend.FriendReqResponseDTO;
import pungmul.pungmul.dto.friend.FriendRequestDTO;
import pungmul.pungmul.dto.member.SimpleUserDTO;
import pungmul.pungmul.dto.message.friend.FriendRequestInvitationMessageDTO;
import pungmul.pungmul.repository.friend.repository.FriendRepository;
import pungmul.pungmul.repository.image.repository.ImageRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.service.file.ImageService;
import pungmul.pungmul.service.member.membermanagement.MemberService;
import pungmul.pungmul.service.message.MessageService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final MemberService memberService;
    private final ImageService imageService;
    private final ImageRepository imageRepository;
    private final MessageService messageService;


    public FriendListResponseDTO getFriendList(UserDetails userDetails) {
        Long userId = userRepository.getUserByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(NoSuchElementException::new);

        List<Friend> friendList = friendRepository.getFriendList(userId);

        // ACCEPTED 상태 친구 목록
        List<FriendRequestDTO> acceptedFriendList = friendList.stream()
                .filter(friend -> friend.getStatus() == FriendStatus.ACCEPTED)
                .map(friend -> toFriendRequestDTO(friend, userId, null)) // ACCEPTED 상태인 경우 isRequestSentByUser에 null 할당
                .collect(Collectors.toList());

        // 내가 보낸 PENDING 상태 친구 요청 목록 (senderId가 내 userId인 경우)
        List<FriendRequestDTO> pendingSentList = friendList.stream()
                .filter(friend -> friend.getStatus() == FriendStatus.PENDING && friend.getSenderId().equals(userId))
                .map(friend -> toFriendRequestDTO(friend, userId, true)) // 내가 요청을 보낸 경우 isRequestSentByUser = true
                .collect(Collectors.toList());

        // 내가 받은 PENDING 상태 친구 요청 목록 (receiverId가 내 userId인 경우)
        List<FriendRequestDTO> pendingReceivedList = friendList.stream()
                .filter(friend -> friend.getStatus() == FriendStatus.PENDING && friend.getReceiverId().equals(userId))
                .map(friend -> toFriendRequestDTO(friend, userId, false)) // 내가 요청을 받은 경우 isRequestSentByUser = false
                .collect(Collectors.toList());

        return FriendListResponseDTO.builder()
                .acceptedFriendList(acceptedFriendList)  // 이미 친구 상태인 목록
                .pendingSentList(pendingSentList)        // 내가 보낸 요청 목록
                .pendingReceivedList(pendingReceivedList) // 내가 받은 요청 목록
                .build();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)  // 트랜잭션이 직렬화되어 동시에 실행되는 트랜잭션 충돌을 방지
    public FriendReqResponseDTO sendFriendRequest(UserDetails userDetails, String receiverUserName) {
        isReqToSelfCheck(userDetails, receiverUserName);

        Long userId = userRepository.getUserByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(NoSuchElementException::new);

        Long receiverId = userRepository.getUserByEmail(receiverUserName)
                .map(User::getId)
                .orElseThrow(NoSuchElementException::new);

        Friend friend = getFriend(userId, receiverId);
        friendRepository.sendFriendRequest(friend);

        // 알림 메시지 생성 및 전송
        FriendRequestInvitationMessageDTO invitationMessage = getInvitationMessage(userDetails, friend.getId(), userId);
        messageService.sendMessage(MessageType.INVITATION, MessageDomainType.FRIEND, receiverUserName, invitationMessage);

        return FriendReqResponseDTO.builder()
                .friendId(friend.getId())
                .build();
    }

    private static Friend getFriend(Long userId, Long receiverId) {
        return Friend.builder()
                .senderId(userId)
                .receiverId(receiverId)
                .build();
    }

    public FriendRequestInvitationMessageDTO getInvitationMessage(UserDetails userDetails,Long friendRequestId, Long userId) {
        String senderName = userRepository.getUserByEmail(userDetails.getUsername()).orElseThrow(NoSuchElementException::new).getName();
        return FriendRequestInvitationMessageDTO.builder()
                .friendRequestId(friendRequestId)
                .senderId(userId)
                .senderName(senderName)  // 실제 이름이 필요하다면 User 엔티티에서 가져올 수 있음
                .content(senderName + "님이 친구 요청을 보냈습니다.")
//                .sentAt(LocalDateTime.now())
                .build();
    }

    @Transactional(readOnly = true)
    public List<AvailableFriendDTO> searchUsersToReqFriend(String keyword, UserDetails userDetails) {

        Long loginUserId = userRepository.getUserByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(NoSuchElementException::new);

        // 사용자가 입력한 keyword로 사용자 검색
        List<User> users = userRepository.searchUsersByKeyword(keyword);

        // 각 사용자의 상태를 확인하고 AvailableFriendDTO로 변환
        return users.stream()
                .filter(user -> !user.getEmail().equals(userDetails.getUsername()))
                .map(user -> {
                    // 친구 관계 확인
                    Optional<Friend> friendRelation = friendRepository.findFriendByUsers(loginUserId, user.getId());

                    // 친구 상태가 ACCEPTED 또는 BLOCK인 경우 제외
                    if (friendRelation.isPresent()) {
                        Friend friend = friendRelation.get();
                        FriendStatus status = friend.getStatus();
                        if (status == FriendStatus.ACCEPTED || status == FriendStatus.BLOCK) {
                            return null; // 이미 친구 상태거나 차단된 경우 제외
                        } else if (status == FriendStatus.PENDING) {
                            FriendRequestFrom requestFrom = friend.getSenderId().equals(loginUserId)
                                    ? FriendRequestFrom.SEND
                                    : FriendRequestFrom.RECEIVE;

                            return AvailableFriendDTO.builder()
                                    .user(toSimpleUserDTO(user.getId()))
                                    .friendRequestFrom(requestFrom) // 누가 보냈는지 정보 추가
                                    .build();
                        }
                    }

                    // 친구 요청이 없는 사용자라면 상태를 NONE으로 설정
                    return AvailableFriendDTO.builder()
                            .user(toSimpleUserDTO(user.getId()))
                            .friendRequestFrom(FriendRequestFrom.NONE) // 친구 요청 상태가 없는 경우 NONE으로 설정
                            .build();
                })
                .filter(Objects::nonNull) // null 제거
                .collect(Collectors.toList());
    }

    private FriendRequestDTO toFriendRequestDTO(Friend friend, Long userId, Boolean isRequestSentByUser) {
        Long otherUserId = getFriendId(friend, userId); // 상대방 userId를 추출
        SimpleUserDTO simpleUserDTO = toSimpleUserDTO(otherUserId); // 상대방 userId로 SimpleUserDTO 생성

        return FriendRequestDTO.builder()
                .friendRequestId(friend.getId())
                .friendStatus(friend.getStatus()) // 상태 그대로 사용
                .simpleUserDTO(simpleUserDTO) // 상대방 정보
                .isRequestSentByUser(isRequestSentByUser) // ACCEPTED일 경우 null, PENDING일 경우 true/false
                .build();
    }

    private static void isReqToSelfCheck(UserDetails userDetails, String receiverUserName) {
        if (userDetails.getUsername().equals(receiverUserName)) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청");
        }
    }

    public void acceptFriendRequest(Long friendRequestId) {
        friendRepository.acceptFriendRequest(friendRequestId);
    }

    public void declineFriendRequest(Long friendRequestId) {
        friendRepository.declineFriendRequest(friendRequestId);
    }

    public void blockFriend(Long friendRequestId) {
        friendRepository.blockFriend(friendRequestId);
    }

    public FriendRequestDTO getFriendRequestDTO(Long userId, Friend friend) {

        Long friendId = getFriendId(friend, userId);    //  양방향 친구 관계에서 상대방의 userId를 가져옴
        SimpleUserDTO simpleUserDTO = memberService.getSimpleUserDTO(friendId); //  친구의 simpleUserDTO

        return FriendRequestDTO.builder()
                .friendRequestId(friend.getId())  // friend의 ID 사용
                .friendStatus(friend.getStatus())  // friend의 상태 그대로 사용
                .simpleUserDTO(simpleUserDTO)  // 상대방의 정보 넣기
                .build();
    }

    private Long getFriendId(Friend friend, Long userId) {
        return friend.getSenderId().equals(userId) ? friend.getReceiverId() : friend.getSenderId();
    }

    private SimpleUserDTO toSimpleUserDTO(Long otherUserId) {
        User otherUser = userRepository.getUserByUserId(otherUserId)
                .orElseThrow(NoSuchElementException::new);

        return SimpleUserDTO.builder()
                .userId(otherUser.getId())
                .username(otherUser.getEmail())
                .name(otherUser.getName())
                .profileImage(
                        imageRepository.getImagesByDomainIdAndType(DomainType.PROFILE, otherUser.getId())
                                .stream().findFirst().orElseGet(imageService::getAnonymousImage)
                )
                .build();
    }
}

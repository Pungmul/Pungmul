package pungmul.pungmul.service.friend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.friend.Friend;
import pungmul.pungmul.domain.friend.FriendStatus;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.dto.friend.FriendListResponseDTO;
import pungmul.pungmul.dto.friend.FriendRequestDTO;
import pungmul.pungmul.dto.member.SimpleUserDTO;
import pungmul.pungmul.repository.friend.repository.FriendRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.service.member.MemberService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final MemberService memberService;

    public FriendListResponseDTO getFriendList(UserDetails userDetails) {
        Long userId = userRepository.getUserByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(NoSuchElementException::new);
        List<Friend> friendList = friendRepository.getFriendList(userId); //  수락된 친구 관계 & 나한테 온 친구 요청 반환
        return getFriendResponseDTO(friendList, userId);
    }

    @Transactional
    public void sendFriendRequest(UserDetails userDetails, String receiverUserName) {
        Long userId = userRepository.getUserByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(NoSuchElementException::new);

        Long receiverId = userRepository.getUserByEmail(receiverUserName)
                .map(User::getId)
                .orElseThrow(NoSuchElementException::new);

        friendRepository.sendFriendRequest(userId, receiverId);
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

    private FriendListResponseDTO getFriendResponseDTO(List<Friend> friendList, Long userId) {
        HashSet<Long> processedFriendIds = new HashSet<>();


        List<FriendRequestDTO> acceptedFriendList = friendList.stream()
                .filter(friend -> friend.getStatus() == FriendStatus.ACCEPTED)
                .filter(friend -> processedFriendIds.add(getFriendId(friend, userId)))
                .map(friend -> getFriendRequestDTO(userId, friend))
                .collect(Collectors.toList());

        List<FriendRequestDTO> requestedFriendList = friendList.stream()
                .filter(friend -> friend.getStatus() == FriendStatus.PENDING)
                .filter(friend -> processedFriendIds.add(getFriendId(friend, userId)))
                .map(friend -> getFriendRequestDTO(userId, friend))
                .collect(Collectors.toList());

//        List<FriendRequestDTO> requestedFriendList = friendList.stream()
//                .filter(friend -> friend.getStatus() == FriendStatus.PENDING)
//                .map(friend -> memberService.getSimpleUserDTO(getFriendId(friend, userId)))
//                .collect(Collectors.toList());
        log.info("requested : {}", requestedFriendList);

        return FriendListResponseDTO.builder()
                .friendList(acceptedFriendList)
                .requestedFriendList(requestedFriendList)
                .build();
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

}

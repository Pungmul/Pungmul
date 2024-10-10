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
import pungmul.pungmul.dto.member.SimpleUserDTO;
import pungmul.pungmul.repository.friend.repository.FriendRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.service.member.MemberService;

import java.util.ArrayList;
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

        for (Friend friend : friendList) {
            log.info("sender : {}, receiver : {}, status : {}", friend.getSenderId(), friend.getReceiverId(), friend.getStatus());
        }
        List<SimpleUserDTO> acceptedFriendList = friendList.stream()
                .filter(friend -> friend.getStatus() == FriendStatus.ACCEPTED)
                .map(friend -> memberService.getSimpleUserDTO(getFriendId(friend, userId)))
                .collect(Collectors.toList());


        List<SimpleUserDTO> requestedFriendList = friendList.stream()
                .filter(friend -> friend.getStatus() == FriendStatus.PENDING)
                .map(friend -> memberService.getSimpleUserDTO(getFriendId(friend, userId)))
                .collect(Collectors.toList());
        log.info("requested : {}", requestedFriendList);

        return FriendListResponseDTO.builder()
                .friendList(acceptedFriendList)
                .requestedFriendList(requestedFriendList)
                .build();
    }

    private Long getFriendId(Friend friend, Long userId) {
        return friend.getSenderId().equals(userId) ? friend.getReceiverId() : friend.getSenderId();
    }

}

package pungmul.pungmul.repository.friend.repository;

import pungmul.pungmul.domain.friend.Friend;

import java.util.List;
import java.util.Optional;

public interface FriendRepository {
    List<Friend> getFriendList(Long userId);

    void sendFriendRequest(Long senderId, Long receiverId);

    void acceptFriendRequest(Long friendRequestId);

    void declineFriendRequest(Long friendRequestId);

    void blockFriend(Long friendRequestId);

    Optional<Friend> findFriendByUsers(Long loginUserId, Long userId);
}

package pungmul.pungmul.repository.friend.repository;

import pungmul.pungmul.domain.friend.Friend;

import java.util.List;

public interface FriendRepository {
    List<Friend> getFriendList(Long userId);

    void sendFriendRequest(Long userId, Long receiverId);

    void acceptFriendRequest(Long friendRequestId);

    void declineFriendRequest(Long friendRequestId);

    void blockFriend(Long friendRequestId);
}

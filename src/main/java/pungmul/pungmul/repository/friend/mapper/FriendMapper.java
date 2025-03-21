package pungmul.pungmul.repository.friend.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.friend.Friend;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FriendMapper {
    List<Friend> getFriendList(Long userId);

    void sendFriendRequest(Friend friend);

    void acceptFriendRequest(Long friendRequestId);

    void declineFriendRequest(Long friendRequestId);

    void blockFriend(Long friendRequestId);

    Optional<Friend> findFriendByUsers(Long loginUserId, Long userId);
}

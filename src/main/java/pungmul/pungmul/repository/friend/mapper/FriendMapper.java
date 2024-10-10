package pungmul.pungmul.repository.friend.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.friend.Friend;

import java.util.List;

@Mapper
public interface FriendMapper {
    List<Friend> getFriendList(Long userId);

    void sendFriendRequest(Long senderId, Long receiverId);

    void acceptFriendRequest(Long friendRequestId);

    void declineFriendRequest(Long friendRequestId);

    void blockFriend(Long friendRequestId);
}

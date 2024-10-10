package pungmul.pungmul.repository.friend.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.friend.Friend;
import pungmul.pungmul.repository.friend.mapper.FriendMapper;
import pungmul.pungmul.repository.friend.repository.FriendRepository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MybatisFriendRepository implements FriendRepository {
    private final FriendMapper friendMapper;

    @Override
    public List<Friend> getFriendList(Long userId) {
        return friendMapper.getFriendList(userId);
    }

    @Override
    public void sendFriendRequest(Long userId, Long receiverId) {
        friendMapper.sendFriendRequest(userId, receiverId);
    }

    @Override
    public void acceptFriendRequest(Long friendRequestId) {
        friendMapper.acceptFriendRequest(friendRequestId);
    }

    @Override
    public void declineFriendRequest(Long friendRequestId) {
        friendMapper.declineFriendRequest(friendRequestId);
    }

    @Override
    public void blockFriend(Long friendRequestId) {
        friendMapper.blockFriend(friendRequestId);
    }
}

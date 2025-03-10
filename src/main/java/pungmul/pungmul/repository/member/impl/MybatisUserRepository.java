package pungmul.pungmul.repository.member.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.core.exception.custom.member.NoSuchUsernameException;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.member.mapper.UserMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MybatisUserRepository implements UserRepository {
    private final UserMapper userMapper;

    @Override
    public void saveUser(User user) {
        userMapper.saveUser(user);
    }

    @Override
    public Optional<User> getUserByUserId(Long userId) {
        return Optional.ofNullable(userMapper.getUserByUserId(userId).masked());
    }

    @Override
    public Optional<User> getUserByAccountId(Long accountId) {
        return Optional.ofNullable(userMapper.getUserByAccountId(accountId).masked());
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.getUserByEmail(email).orElseThrow(() -> new NoSuchUsernameException(email)).masked();
//        return Optional.ofNullable(userMapper.getUserByEmail(email).masked());
    }

    public Long getUserIdByAccountId(Long accountId) {
        User user = getUserByAccountId(accountId)
                .orElseThrow(NoSuchElementException::new);

        return user.getId();
    }

    @Override
    public List<User> searchUsersByKeyword(String keyword) {
        return userMapper.searchUsersByKeyword(keyword).stream().map(User::masked).toList();
    }

    @Override
    public void updateUser(User updateUser) {
        userMapper.updateUser(updateUser);
    }

    @Override
    public void deleteUser(String email) {
        userMapper.deleteUser(email);
    }

    @Override
    public void banUser(String username) {
        userMapper.banUser(username);
    }
}

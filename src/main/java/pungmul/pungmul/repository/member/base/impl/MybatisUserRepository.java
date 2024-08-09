package pungmul.pungmul.repository.member.base.impl;

import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.User;
import pungmul.pungmul.repository.member.base.repository.UserRepository;
import pungmul.pungmul.repository.member.base.mapper.UserMapper;

import java.util.Optional;

@Repository
public class MybatisUserRepository implements UserRepository {
    private final UserMapper userMapper;

    public MybatisUserRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void saveUser(User user) {
        userMapper.saveUser(user);
    }

    @Override
    public Optional<User> getUserByUserId(Long userId) {
        return Optional.ofNullable(userMapper.getUserByUserId(userId));
    }

    @Override
    public Optional<User> getUserByAccountId(Long accountId) {
        return Optional.ofNullable(userMapper.getUserByAccountId(accountId));
    }

    @Override
    public Optional<User> getUserByLoginId(String loginId) {
        return Optional.ofNullable(userMapper.getUserByLoginId(loginId));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userMapper.getUserByEmail(email));
    }
}

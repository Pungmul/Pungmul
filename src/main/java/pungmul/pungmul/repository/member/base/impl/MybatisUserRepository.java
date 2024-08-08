package pungmul.pungmul.repository.member.base.impl;

import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.User;
import pungmul.pungmul.repository.member.base.repository.UserRepository;
import pungmul.pungmul.repository.member.base.mapper.UserMapper;

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
    public User getUserByUserId(Long userId) {
        return userMapper.getUserByUserId(userId);
    }

    @Override
    public User getUserByAccountId(Long accountId) {
        return userMapper.getUserByAccountId(accountId);
    }

    @Override
    public User getUserByLoginId(String loginId) {
        return userMapper.getUserByLoginId(loginId);
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }
}

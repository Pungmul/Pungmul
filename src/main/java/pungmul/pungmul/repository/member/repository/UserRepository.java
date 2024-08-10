package pungmul.pungmul.repository.member.repository;

import pungmul.pungmul.domain.member.User;

import java.util.Optional;

public interface UserRepository {
    void saveUser(User user);

    public Optional<User> getUserByUserId(Long userId);

    public Optional<User> getUserByAccountId(Long accountId);

    public Optional<User> getUserByLoginId(String loginId);

    public Optional<User> getUserByEmail(String email);
}

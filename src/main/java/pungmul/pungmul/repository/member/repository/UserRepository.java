package pungmul.pungmul.repository.member.repository;

import pungmul.pungmul.domain.member.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void saveUser(User user);

    public Optional<User> getUserByUserId(Long userId);

    public Optional<User> getUserByAccountId(Long accountId);

    User getUserByEmail(String email);

    public Long getUserIdByAccountId(Long accountId);

    List<User> searchUsersByKeyword(String keyword);

    void updateUser(User updateUser);

    void deleteUser(String email);

    void banUser(String username);
}

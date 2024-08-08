package pungmul.pungmul.repository.member.base.repository;

import pungmul.pungmul.domain.member.User;

public interface UserRepository {
    void saveUser(User user);

    public User getUserByUserId(Long userId);

    public User getUserByAccountId(Long accountId);

    public User getUserByLoginId(String loginId);

    public User getUserByEmail(String email);
}

package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.member.user.User;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    void saveUser(User user);

    User getUserByUserId(Long id);

    User getUserByAccountId(Long accountId);

    Optional<User> getUserByEmail(String email);

    List<User> searchUsersByKeyword(String keyword);

    void updateUser(User updateUser);

    void deleteUser(String email);

    void banUser(String username);
}

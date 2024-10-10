package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.member.user.User;

@Mapper
public interface UserMapper {
    void saveUser(User user);

    User getUserByUserId(Long id);

//    User getUserByLoginId(String loginId);

    User getUserByAccountId(Long accountId);

    User getUserByEmail(String email);

//    Long selectLastInsertId();

}

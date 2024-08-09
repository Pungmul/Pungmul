package pungmul.pungmul.repository.member.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.member.User;

@Mapper
public interface UserMapper {
    void saveUser(User user);

    User getUserByUserId(Long id);

    User getUserByLoginId(String loginId);

    User getUserByAccountId(Long accountId);

    User getUserByEmail(String email);

//    Long selectLastInsertId();

}

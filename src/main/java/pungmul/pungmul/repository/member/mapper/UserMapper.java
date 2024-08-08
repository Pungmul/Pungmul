package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.member.User;

@Mapper
public interface UserMapper {
    Long saveUser(User user);

    User getUserById(Long id);

    Long selectLastInsertId();

    User getUserByAccountId(Long accountId);

}

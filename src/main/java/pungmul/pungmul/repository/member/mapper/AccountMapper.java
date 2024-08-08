package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.LoginForm;

@Mapper
public interface AccountMapper {
    Long saveAccount(Account account);

    Account getAccountById(Long id);

}

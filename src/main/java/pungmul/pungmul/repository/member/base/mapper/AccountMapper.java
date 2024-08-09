package pungmul.pungmul.repository.member.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.LoginForm;

@Mapper
public interface AccountMapper {

    void saveAccount(Account account);

    Account getAccountByAccountId(Long accountId);

    Account getAccountByLoginId(String loginId);

    Account getAccountByEmail(String email);

//    Long selectLastInsertAccountId();
}

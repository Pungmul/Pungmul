package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.member.account.Account;

@Mapper
public interface AccountMapper {

    void saveAccount(Account account);

    Account getAccountByAccountId(Long accountId);

    Account getAccountByLoginId(String loginId);

    Account getAccountByEmail(String email);

//    Long selectLastInsertAccountId();
}

package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.member.account.Account;

@Mapper
public interface AccountMapper {

    void saveAccount(Account account);

    Account getAccountByAccountId(Long accountId);

    Account getAccountByLoginIdForLogin(String loginId);

    Account getAccountByLoginId(String loginId);

    Account getAccountByEmail(String email);

    void setEnabledAccount(Long accountId);

    void updatePassword(Long accountId, String password);

    void deleteAccount(String loginId);

    void banAccount(String username);

    void updateAccount(Account account);

    void unlockAccount(Long id);

//    Long selectLastInsertAccountId();
}

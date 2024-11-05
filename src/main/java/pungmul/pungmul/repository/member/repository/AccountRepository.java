package pungmul.pungmul.repository.member.repository;

import pungmul.pungmul.domain.member.account.Account;

import java.util.Optional;

public interface AccountRepository {
    void saveAccount(Account account);

    public Optional<Account> getAccountByAccountId(Long accountId);

    public Optional<Account> getAccountByLoginIdForLogin(String loginId);

    public Optional<Account> getAccountByLoginId(String loginId);

    public Optional<Account> getAccountByEmail(String email);

    public void setEnabledAccount(Long accountId);

    void updatePassword(Long accountId, String password);

    void deleteAccount(String loginId);
}

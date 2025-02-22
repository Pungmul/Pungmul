package pungmul.pungmul.repository.member.repository;

import pungmul.pungmul.domain.member.account.Account;

import java.util.Optional;

public interface AccountRepository {
    void saveAccount(Account account);

    public Optional<Account> getAccountByAccountId(Long accountId);

    public Optional<Account> getAccountByAccountId(String username);

    public Optional<Account> getAccountByUsernameForLogin(String username);

    public Optional<Account> getAccountByUsername(String username);

    public Optional<Account> getAccountByEmail(String email);

    public void setEnabledAccount(Long accountId);

    void updatePassword(Long accountId, String password);

    void deleteAccount(String username);

    void banAccount(String username);

    void updateAccount(Account account);

    void unlockAccount(Long id);

    Boolean checkDuplicateUsername(String username);
}

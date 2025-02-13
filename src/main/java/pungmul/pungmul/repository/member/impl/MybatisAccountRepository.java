package pungmul.pungmul.repository.member.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.mapper.AccountMapper;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MybatisAccountRepository implements AccountRepository {
    private final AccountMapper accountMapper;

    @Override
    public void saveAccount(Account account) {
        accountMapper.saveAccount(account);
    }


    @Override
    public Optional<Account> getAccountByAccountId(Long accountId) {
        return Optional.ofNullable(accountMapper.getAccountByAccountId(accountId));
    }

    @Override
    public Optional<Account> getAccountByAccountId(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<Account> getAccountByUsernameForLogin(String username) {
        return Optional.ofNullable(accountMapper.getAccountByUsernameForLogin(username));
    }

    @Override
    public Optional<Account> getAccountByUsername(String username) {
        return Optional.ofNullable(accountMapper.getAccountByUsername(username));
    }

    @Override
    public Optional<Account> getAccountByEmail(String email) {
        return Optional.ofNullable(accountMapper.getAccountByEmail(email));
    }

    @Override
    public void setEnabledAccount(Long accountId) {
        accountMapper.setEnabledAccount(accountId);
    }

    @Override
    public void updatePassword(Long accountId, String password) {
        accountMapper.updatePassword(accountId, password);
    }

    @Override
    public void deleteAccount(String username) {
        accountMapper.deleteAccount(username);
    }

    @Override
    public void banAccount(String username) {
        accountMapper.banAccount(username);
    }

    @Override
    public void updateAccount(Account account) {
        accountMapper.updateAccount(account);
    }

    @Override
    public void unlockAccount(Long id) {
        accountMapper.unlockAccount(id);
    }

    @Override
    public Boolean checkDuplicateUsername(String username) {
        return accountMapper.checkDuplicateUsername(username);
    }
}

package pungmul.pungmul.repository.member.impl;

import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.mapper.AccountMapper;

import java.util.Optional;

@Repository
public class MybatisAccountRepository implements AccountRepository {
    private final AccountMapper accountMapper;

    public MybatisAccountRepository(AccountMapper accountMapper){
        this.accountMapper = accountMapper;
    }

    @Override
    public void saveAccount(Account account) {
        accountMapper.saveAccount(account);
    }

    @Override
    public Optional<Account> getAccountByAccountId(Long accountId) {
        return Optional.ofNullable(accountMapper.getAccountByAccountId(accountId));
    }

    @Override
    public Optional<Account> getAccountByLoginId(String loginId) {
        return Optional.ofNullable(accountMapper.getAccountByLoginId(loginId));
    }

    @Override
    public Optional<Account> getAccountByEmail(String email) {
        return Optional.ofNullable(accountMapper.getAccountByEmail(email));
    }
}

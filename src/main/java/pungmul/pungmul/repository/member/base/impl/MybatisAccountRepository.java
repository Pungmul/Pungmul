package pungmul.pungmul.repository.member.base.impl;

import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.repository.member.base.repository.AccountRepository;
import pungmul.pungmul.repository.member.base.mapper.AccountMapper;

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
    public Account getAccountByAccountId(Long accountId) {
        return accountMapper.getAccountByAccountId(accountId);
    }

    @Override
    public Account getAccountByLoginId(String loginId) {
        return accountMapper.getAccountByLoginId(loginId);
    }

    @Override
    public Account getAccountByEmail(String email) {
        return accountMapper.getAccountByEmail(email);
    }
}

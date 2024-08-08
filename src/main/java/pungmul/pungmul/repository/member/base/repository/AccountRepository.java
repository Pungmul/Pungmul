package pungmul.pungmul.repository.member.base.repository;

import pungmul.pungmul.domain.member.Account;

public interface AccountRepository {
    void saveAccount(Account account);

    public Account getAccountByAccountId(Long accountId);

    public Account getAccountByLoginId(String loginId);

    public Account getAccountByEmail(String email);

    }

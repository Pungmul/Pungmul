package pungmul.pungmul.repository.member.login;

import pungmul.pungmul.domain.member.Account;

import java.util.Optional;

public interface LoginRepository {

    Optional<Account> findByLoginId(String loginId);
}

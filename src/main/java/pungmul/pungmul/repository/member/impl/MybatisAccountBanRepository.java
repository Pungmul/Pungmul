package pungmul.pungmul.repository.member.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.auth.AccountBan;
import pungmul.pungmul.repository.member.mapper.AccountBanMapper;
import pungmul.pungmul.repository.member.repository.AccountBanRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class MybatisAccountBanRepository implements AccountBanRepository {
    private final AccountBanMapper accountBanMapper;

    @Override
    public void insertAccountBan(AccountBan accountBan) {
        accountBanMapper.insertAccountBan(accountBan);
    }

    @Override
    public Optional<AccountBan> getAccountBanById(Long id) {
        return accountBanMapper.getAccountBanById(id);
    }

    @Override
    public Optional<AccountBan> getAccountBanByUsername(String username) {
        return accountBanMapper.getAccountBanByUsername(username);
    }

    @Override
    public List<AccountBan> getActiveAccountBans() {
        return accountBanMapper.getActiveAccountBans();
    }

    @Override
    public List<AccountBan> getAccountBansByUserId(Long userId) {
        return accountBanMapper.getAccountBansByUserId(userId);
    }

    @Override
    public void updateAccountBan(AccountBan accountBan) {
        accountBanMapper.updateAccountBan(accountBan);
    }

    @Override
    public void deactivateExpiredAccountBans(LocalDateTime now) {
        accountBanMapper.deactivateExpiredAccountBans(now);
    }

    @Override
    public void deleteAccountBanById(Long id) {
        accountBanMapper.deleteAccountBanById(id);
    }

    @Override
    public void deActivateAccountBan(Long id) {
        accountBanMapper.deActivateAccountBan(id);
    }
}

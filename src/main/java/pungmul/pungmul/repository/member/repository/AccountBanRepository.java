package pungmul.pungmul.repository.member.repository;

import pungmul.pungmul.domain.member.auth.AccountBan;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AccountBanRepository {

        void insertAccountBan(AccountBan accountBan);

        Optional<AccountBan> getAccountBanById(Long id);

        Optional<AccountBan> getAccountBanByUsername(String username);

        List<AccountBan> getActiveAccountBans();

        List<AccountBan> getAccountBansByUserId(Long userId);

        void updateAccountBan(AccountBan accountBan);

        void deactivateExpiredAccountBans(LocalDateTime now);

        void deleteAccountBanById(Long id);

        void deActivateAccountBan(Long id);
}


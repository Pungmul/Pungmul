package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.member.auth.AccountBan;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface AccountBanMapper {
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

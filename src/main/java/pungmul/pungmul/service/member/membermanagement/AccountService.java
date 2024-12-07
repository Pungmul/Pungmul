package pungmul.pungmul.service.member.membermanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pungmul.pungmul.core.exception.custom.member.CustomAccountLockedException;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.domain.member.account.UserRole;
import pungmul.pungmul.domain.member.auth.AccountBan;
import pungmul.pungmul.dto.member.BanMemberRequestDTO;
import pungmul.pungmul.dto.member.CreateMemberRequestDTO;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.repository.AccountBanRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountBanRepository accountBanRepository;

    public Long createAccount(CreateMemberRequestDTO createMemberRequestDTO) {
        Account account = Account.builder()
                .loginId(createMemberRequestDTO.getLoginId())
                .password(passwordEncoder.encode(createMemberRequestDTO.getPassword()))
                .roles(Set.of(UserRole.ROLE_USER))
                .build();
        accountRepository.saveAccount(account);
        return account.getId();
    }

    public void updatePassword(String email, String newPassword) {
        Long accountId = accountRepository.getAccountByLoginId(email)
                .map(Account::getId)
                .orElseThrow(() -> new NoSuchElementException("Account not found"));
        accountRepository.updatePassword(accountId, passwordEncoder.encode(newPassword));
    }

    public void deleteAccount(String email) {
        accountRepository.deleteAccount(email);
    }

    public void enableAccount(Long accountId) {
        accountRepository.setEnabledAccount(accountId);
    }

    public void banAccount(BanMemberRequestDTO banMemberRequestDTO) {
        accountRepository.banAccount(banMemberRequestDTO.getUsername());
        accountBanRepository.insertAccountBan(
                AccountBan.builder()
                        .username(banMemberRequestDTO.getUsername())
                        .banReason(banMemberRequestDTO.getBanReason())
                        .banEndTime(banMemberRequestDTO.getBanUntil())
                        .isActive(Boolean.TRUE)
                        .build()
        );
    }

    public Account unlockAccount(Account account) {
        // 현재 시간이 정지 해제 시간(banEndTime) 이후인지 확인
        LocalDateTime now = LocalDateTime.now();
        AccountBan accountBan = accountBanRepository.getAccountBanByUsername(account.getLoginId())
                .orElseThrow(() -> new NoSuchElementException("Account not found"));

        if (accountBan.getBanEndTime() != null && now.isAfter(accountBan.getBanEndTime())) {
            // 정지 해제 처리
            accountBanRepository.deActivateAccountBan(accountBan.getId());
            accountRepository.unlockAccount(account.getId());
        } else {
            throw new CustomAccountLockedException("Account locked");
        }
        return account;
    }
}

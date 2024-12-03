package pungmul.pungmul.service.member.membermanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.domain.member.account.UserRole;
import pungmul.pungmul.dto.member.CreateMemberRequestDTO;
import pungmul.pungmul.repository.member.repository.AccountRepository;

import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

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
}

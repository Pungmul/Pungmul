package pungmul.pungmul.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.repository.member.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.getAccountByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException(username));
        return UserDetailsImpl.builder()
                .loginId(account.getLoginId())
                .password(account.getPassword())
                .authorities(account.getRoles())
                .build();
    }
}


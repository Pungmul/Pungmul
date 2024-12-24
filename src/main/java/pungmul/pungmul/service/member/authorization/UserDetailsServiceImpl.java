package pungmul.pungmul.service.member.authorization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.repository.member.repository.AccountRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.getAccountByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found : {}" + username));

        return UserDetailsImpl.builder()
                .accountId(account.getId())
                .loginId(account.getUsername())
                .password(account.getPassword())
                .authorities(account.getRoles())
                .build();
    }
}


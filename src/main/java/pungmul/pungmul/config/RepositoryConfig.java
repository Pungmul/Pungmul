package pungmul.pungmul.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pungmul.pungmul.repository.member.base.impl.MybatisInstrumentStatusRepository;
import pungmul.pungmul.repository.member.base.impl.MybatisUserRepository;
import pungmul.pungmul.repository.member.base.repository.AccountRepository;
import pungmul.pungmul.repository.member.base.impl.MybatisAccountRepository;
import pungmul.pungmul.repository.member.base.repository.InstrumentStatusRepository;
import pungmul.pungmul.repository.member.base.repository.UserRepository;
import pungmul.pungmul.repository.member.signup.CreateMemberMapper;
import pungmul.pungmul.repository.member.signup.CreateMemberRepository;
import pungmul.pungmul.repository.member.base.mapper.AccountMapper;
import pungmul.pungmul.repository.member.base.mapper.InstrumentStatusMapper;
import pungmul.pungmul.repository.member.base.mapper.UserMapper;

@Configuration
@RequiredArgsConstructor
public class RepositoryConfig {
    private final CreateMemberMapper createMemberMapper;
    private final AccountMapper accountMapper;
    private final UserMapper userMapper;
    private final InstrumentStatusMapper instrumentStatusMapper;

    @Bean
    public AccountRepository accountRepository(){
        return new MybatisAccountRepository(accountMapper);
    }

    @Bean
    public UserRepository userRepository(){
        return new MybatisUserRepository(userMapper);
    }

    public InstrumentStatusRepository instrumentStatusRepository(){
        return new MybatisInstrumentStatusRepository(instrumentStatusMapper);
    }

}

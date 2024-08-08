package pungmul.pungmul.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pungmul.pungmul.repository.member.CreateMemberRepository;
import pungmul.pungmul.repository.member.MybatisCreateMemberRepository;
import pungmul.pungmul.repository.member.mapper.AccountMapper;
import pungmul.pungmul.repository.member.mapper.InstrumentStatusMapper;
import pungmul.pungmul.repository.member.mapper.UserMapper;

@Configuration
@RequiredArgsConstructor
public class RepositoryConfig {
    private final AccountMapper accountMapper;
    private final UserMapper userMapper;
    private final InstrumentStatusMapper instrumentStatusMapper;
    @Bean
    public CreateMemberRepository createMemberRepository() {
        return new MybatisCreateMemberRepository(accountMapper, userMapper, instrumentStatusMapper);
    }

}

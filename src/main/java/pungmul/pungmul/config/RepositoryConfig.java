package pungmul.pungmul.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pungmul.pungmul.repository.member.impl.MybatisInstrumentStatusRepository;
import pungmul.pungmul.repository.member.impl.MybatisUserRepository;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.impl.MybatisAccountRepository;
import pungmul.pungmul.repository.member.repository.InstrumentStatusRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.member.mapper.AccountMapper;
import pungmul.pungmul.repository.member.mapper.InstrumentStatusMapper;
import pungmul.pungmul.repository.member.mapper.UserMapper;
import pungmul.pungmul.repository.post.impl.MybatisContentRepository;
import pungmul.pungmul.repository.post.impl.MybatisPostRepository;
import pungmul.pungmul.repository.post.mapper.ContentMapper;
import pungmul.pungmul.repository.post.mapper.PostMapper;
import pungmul.pungmul.repository.post.repository.ContentRepository;
import pungmul.pungmul.repository.post.repository.PostRepository;

@Configuration
@RequiredArgsConstructor
public class RepositoryConfig {
    private final AccountMapper accountMapper;
    private final UserMapper userMapper;
    private final InstrumentStatusMapper instrumentStatusMapper;
    private final PostMapper postMapper;
    private final ContentMapper contentMapper;

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

    @Bean
    public PostRepository postRepository(){
        return new MybatisPostRepository(postMapper);
    }

    @Bean
    public ContentRepository contentRepository(){
        return new MybatisContentRepository(contentMapper);
    }

}

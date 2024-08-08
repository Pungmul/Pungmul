package pungmul.pungmul.repository.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.InstrumentStatus;
import pungmul.pungmul.domain.member.LoginForm;
import pungmul.pungmul.domain.member.User;
import pungmul.pungmul.repository.member.mapper.AccountMapper;
import pungmul.pungmul.repository.member.mapper.InstrumentStatusMapper;
import pungmul.pungmul.repository.member.mapper.UserMapper;

@Slf4j
@Repository
//@RequiredArgsConstructor
public class MybatisCreateMemberRepository implements CreateMemberRepository{
    private final AccountMapper accountMapper;
    private final UserMapper userMapper;
    private final InstrumentStatusMapper instrumentStatusMapper;

    public MybatisCreateMemberRepository (AccountMapper accountMapper, UserMapper userMapper, InstrumentStatusMapper instrumentStatusMapper) {
        this.accountMapper = accountMapper;
        this.userMapper = userMapper;
        this.instrumentStatusMapper = instrumentStatusMapper;
    }

    public Account saveAccount(Account account) {
        Long createdAccountId = accountMapper.saveAccount(account);
        log.info("Account created: {}", createdAccountId);

        return accountMapper.getAccountById(createdAccountId);
    }

    public User saveUser(User user) {
        userMapper.saveUser(user);
        Long createUserId = userMapper.selectLastInsertId();
        log.info("User ID created: {}", createUserId);
        User userById = userMapper.getUserById(createUserId);
        log.info("User name created: {}", userById.getName());
        return userById;
    }

    @Override
    public void saveInstrument(InstrumentStatus instrumentStatus) {
        instrumentStatusMapper.saveInstrument(instrumentStatus);
    }
}

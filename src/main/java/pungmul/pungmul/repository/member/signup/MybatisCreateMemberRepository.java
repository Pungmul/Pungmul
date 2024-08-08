package pungmul.pungmul.repository.member.signup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.InstrumentStatus;
import pungmul.pungmul.domain.member.User;
import pungmul.pungmul.repository.member.base.mapper.AccountMapper;
import pungmul.pungmul.repository.member.base.mapper.InstrumentStatusMapper;
import pungmul.pungmul.repository.member.base.mapper.UserMapper;

//@Slf4j
//@Repository
//@RequiredArgsConstructor
//public class MybatisCreateMemberRepository implements CreateMemberRepository{
//    private final CreateMemberMapper createMemberMapper;
//    private final AccountMapper accountMapper;
//    private final UserMapper userMapper;
//    private final InstrumentStatusMapper instrumentStatusMapper;
//
//    public MybatisCreateMemberRepository (CreateMemberMapper createMemberMapper, AccountMapper accountMapper, UserMapper userMapper, InstrumentStatusMapper instrumentStatusMapper) {
//        this.createMemberMapper = createMemberMapper;
//        this.accountMapper = accountMapper;
//        this.userMapper = userMapper;
//        this.instrumentStatusMapper = instrumentStatusMapper;
//    }
//
//    public Account saveAccount(Account account) {
//        createMemberMapper.saveAccount(account);
//        Long createdAccountId = accountMapper.selectLastInsertAccountId();
//        log.info("Account created: {}", createdAccountId);
//
//        return accountMapper.getAccountByAccountId(createdAccountId);
//    }
//
//    public User saveUser(User user) {
//        userMapper.saveUser(user);
//        Long createUserId = userMapper.selectLastInsertUserId();
//        log.info("User ID created: {}", createUserId);
//        User userById = userMapper.getUserById(createUserId);
//        log.info("User name created: {}", userById.getName());
//        return userById;
//    }
//
//    @Override
//    public void saveInstrument(InstrumentStatus instrumentStatus) {
//        instrumentStatusMapper.saveInstrument(instrumentStatus);
//    }
//}

package pungmul.pungmul.repository.member.signup;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.InstrumentStatus;
import pungmul.pungmul.domain.member.User;

@Mapper
public interface CreateMemberMapper {
    public Long saveAccount(Account account);

    public Long saveUser(User user);

    void saveInstrument(InstrumentStatus instrumentStatus, Long userId);
}

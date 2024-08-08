package pungmul.pungmul.repository.member;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.InstrumentStatus;
import pungmul.pungmul.domain.member.User;

@Mapper
public interface CreateMemberMapper {
    public Long saveAccount(String loginId, String password);

    public Long saveUser(User user);

    public Account getAccountById(Long loginId);

    public User getUserById(Long loginId);

    void saveInstrument(InstrumentStatus instrumentStatus, Long userId);
}

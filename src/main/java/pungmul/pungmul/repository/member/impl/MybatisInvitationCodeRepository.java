package pungmul.pungmul.repository.member.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.invitation.InvitationCode;
import pungmul.pungmul.repository.member.mapper.InvitationCodeMapper;
import pungmul.pungmul.repository.member.repository.InvitationCodeRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MybatisInvitationCodeRepository implements InvitationCodeRepository {
    private final InvitationCodeMapper invitationCodeMapper;
    @Override
    public Optional<InvitationCode> getCodeByValue(String code) {
        return Optional.ofNullable(invitationCodeMapper.getCodeValue(code));
    }

    @Override
    public void insertCode(InvitationCode code) {
        invitationCodeMapper.insertCode(code);
    }

    @Override
    public void decrementRemainingUses(Long id) {
        invitationCodeMapper.decrementRemainingUses(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return invitationCodeMapper.existsByCode(code);
    }
}

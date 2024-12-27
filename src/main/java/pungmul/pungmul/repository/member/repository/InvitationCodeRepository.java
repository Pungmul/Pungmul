package pungmul.pungmul.repository.member.repository;

import pungmul.pungmul.domain.member.invitation.InvitationCode;

import java.util.Optional;

public interface InvitationCodeRepository {

    Optional<InvitationCode> getCodeByValue(String code);

    void insertCode(InvitationCode code);

    void decrementRemainingUses(Long id);

    boolean existsByCode(String code);
}

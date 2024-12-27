package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import pungmul.pungmul.domain.member.invitation.InvitationCode;

@Mapper
public interface InvitationCodeMapper {
    InvitationCode getCodeValue(String code);

    void insertCode(InvitationCode code);

    void decrementRemainingUses(Long id);

    boolean existsByCode(String code);
}

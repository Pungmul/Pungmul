package pungmul.pungmul.service.member.membermanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.core.exception.custom.member.ExpiredInvitationCodeException;
import pungmul.pungmul.core.exception.custom.member.InvalidInvitationCodeException;
import pungmul.pungmul.domain.member.invitation.InvitationCode;
import pungmul.pungmul.dto.member.CreateMemberRequestDTO;
import pungmul.pungmul.repository.member.repository.InvitationCodeRepository;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class InvitationCodeService {
    private final InvitationCodeRepository invitationCodeRepository;

    private final static int DEFAULT_INVITATION_CODE_MAX_USES = 3;

    @Transactional
    public String issueInvitationCode(int maxUses) {
        String uniqueCode = generateNumericCode();

        InvitationCode adminInvitationCode = InvitationCode.builder()
                .code(uniqueCode)
                .issuedBy(null) // 운영자 발급은 null
                .maxUses(maxUses)
                .remainingUses(maxUses)
                .build();

        invitationCodeRepository.insertCode(adminInvitationCode);
        return adminInvitationCode.getCode();
    }

    public void checkInvitationCode(CreateMemberRequestDTO createMemberRequestDTO) {
        InvitationCode invitationCode = invitationCodeRepository.getCodeByValue(createMemberRequestDTO.getInvitationCode()).orElseThrow(InvalidInvitationCodeException::new);
        if (invitationCode.canBeUsed()){
            invitationCodeRepository.decrementRemainingUses(invitationCode.getId());
        } else {
            throw new ExpiredInvitationCodeException();
        }
        invitationCodeRepository.decrementRemainingUses(invitationCode.getId());
    }

    public InvitationCode getInvitationCode(Long userId) {
        InvitationCode generatedInvitationCode = InvitationCode.builder()
                .code(generateNumericCode())
                .maxUses(DEFAULT_INVITATION_CODE_MAX_USES)
                .remainingUses(DEFAULT_INVITATION_CODE_MAX_USES)
                .issuedBy(userId)
                .build();
        invitationCodeRepository.insertCode(generatedInvitationCode);
        return generatedInvitationCode;
    }

    private String generateNumericCode() {
        String code;
        do {
            code = String.format("%06d", new Random().nextInt(1000000));
        } while (invitationCodeRepository.existsByCode(code)); // 이미 존재하는 코드인지 확인
        return code;    }
}

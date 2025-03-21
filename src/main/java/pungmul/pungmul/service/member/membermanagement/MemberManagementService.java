package pungmul.pungmul.service.member.membermanagement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.exception.custom.member.ExpiredInvitationCodeException;
import pungmul.pungmul.core.exception.custom.member.InvalidInvitationCodeException;
import pungmul.pungmul.domain.member.club.Club;
import pungmul.pungmul.domain.member.instrument.InstrumentStatus;
import pungmul.pungmul.domain.member.invitation.InvitationCode;
import pungmul.pungmul.dto.member.*;
import pungmul.pungmul.repository.member.impl.MybatisClubRepository;
import pungmul.pungmul.repository.member.repository.ClubRepository;
import pungmul.pungmul.repository.member.repository.InvitationCodeRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberManagementService {

    private final AccountService accountService;
    private final UserService userService;
    private final InstrumentService instrumentService;
    private final UserImageService userImageService;
    private final ClubRepository clubRepository;
    private final InvitationCodeRepository invitationCodeRepository;
    private final MemberService memberService;
    private final InvitationCodeService invitationCodeService;

    private final static int DEFAULT_INVITATION_CODE_MAX_USES = 3;
    private final static String INVITATION_CODE = "sample";

    /**
     * 회원 생성
     */
    @Transactional
    public CreateAccountResponseDTO createMember(CreateMemberRequestDTO createMemberRequestDTO, MultipartFile profile) throws IOException {
        invitationCodeService.checkInvitationCode(createMemberRequestDTO);
        // 1. 계정 생성
        Long accountId = accountService.createAccount(createMemberRequestDTO);

        // 2. 사용자 생성
        Long userId = userService.createUser(createMemberRequestDTO, accountId);

        // 3. 프로필 이미지 설정
        if (profile != null && !profile.isEmpty()) {
            userImageService.updateProfileImage(userId, profile);
        }

        // 4. 계정 활성화
        accountService.enableAccount(accountId);

        //  5. 초대 코드 발급
        InvitationCode generatedInvitationCode = invitationCodeService.getInvitationCode(userId);

        return CreateAccountResponseDTO.builder()
                .memberDTO(memberService.getMemberInfo(createMemberRequestDTO.getUsername()))
                .invitationCode(generatedInvitationCode.getCode())
                .build();
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public UpdateMemberResponseDTO updateMember(UserDetails userDetails, UpdateMemberRequestDTO updateMemberRequestDTO, MultipartFile profile) throws IOException {
        // 1. 비밀번호 변경
        if (updateMemberRequestDTO.getPassword() != null && !updateMemberRequestDTO.getPassword().isEmpty()) {
            accountService.updatePassword(userDetails.getUsername(), updateMemberRequestDTO.getPassword());
        }

        // 2. 사용자 정보 변경
        userService.updateUserInfo(userDetails.getUsername(), updateMemberRequestDTO);

        // 3. 프로필 이미지 변경
        if (profile != null && !profile.isEmpty()) {
            Long userId = userService.getUserByEmail(userDetails.getUsername()).getId();
            userImageService.updateProfileImage(userId, profile);
        }

        return userService.getUpdateMemberResponse(userDetails.getUsername());
    }

    /**
     * 악기 등록
     */
    @Transactional
    public List<Long> createInstrument(Long accountId, List<InstrumentStatus> instrumentStatusList) {
        return instrumentService.createInstrument(accountId, instrumentStatusList);
    }

    /**
     * 악기 상태 업데이트
     */
    @Transactional
    public UpdateInstrumentResponseDTO updateInstrumentStatus(UserDetails userDetails, UpdateInstrumentRequestDTO updateInstrumentRequestDTO) {
        return instrumentService.updateInstrumentStatus(userDetails.getUsername(), updateInstrumentRequestDTO);
    }

    /**
     * 회원 삭제
     */
    @Transactional
    public void deleteMember(UserDetails userDetails) {
        accountService.deleteAccount(userDetails.getUsername());
        userService.deleteUser(userDetails.getUsername());
    }

    @Transactional
    public BanMemberResponseDTO banMember(BanMemberRequestDTO banMemberRequestDTO) {
        accountService.banAccount(banMemberRequestDTO);
        return BanMemberResponseDTO.builder()
                .name(userService.getUserByEmail(banMemberRequestDTO.getUsername()).getName())
                .banReason(banMemberRequestDTO.getBanReason())
                .banUntil(banMemberRequestDTO.getBanUntil())
                .build();
    }

    public ClubListResponseDTO getClubList() {
        List<Club> clubList = clubRepository.getClubList();

        List<ClubInfo> clubInfoList = clubList.stream()
                .map(club -> ClubInfo.builder()
                        .clubId(club.getId())
                        .school(club.getSchool())
                        .clubName(club.getName())
                        .build())
                .toList();

        return ClubListResponseDTO.builder()
                .clubInfoList(clubInfoList)
                .build();
    }

    public CheckDuplicateUsernameResponseDTO checkDuplicateUsername(String username) {
        Boolean isRegistered = accountService.checkDuplicateUsername(username);

        return CheckDuplicateUsernameResponseDTO.builder()
                .isRegistered(isRegistered)
                .build();
    }

    @Transactional
    public void updatePassword(UserDetailsImpl userDetails, UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        accountService.updatePassword(userDetails.getUsername(), updatePasswordRequestDTO.getPassword());
    }
}

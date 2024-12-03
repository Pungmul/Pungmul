package pungmul.pungmul.service.member.membermanagement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.member.instrument.InstrumentStatus;
import pungmul.pungmul.dto.member.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberManagementService {

    private final AccountService accountService;
    private final UserService userService;
    private final InstrumentService instrumentService;
    private final UserImageService userImageService;

    /**
     * 회원 생성
     */
    @Transactional
    public CreateAccountResponseDTO createMember(CreateMemberRequestDTO createMemberRequestDTO, MultipartFile profile) throws IOException {
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

        return CreateAccountResponseDTO.builder()
                .loginId(createMemberRequestDTO.getLoginId())
                .userName(createMemberRequestDTO.getName())
                .redirectUrl("/login-jwt")
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
    public void deleteUser(UserDetails userDetails) {
        accountService.deleteAccount(userDetails.getUsername());
        userService.deleteUser(userDetails.getUsername());
    }
}

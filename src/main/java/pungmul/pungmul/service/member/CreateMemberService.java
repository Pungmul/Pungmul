package pungmul.pungmul.service.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.InstrumentStatus;
import pungmul.pungmul.domain.member.User;
import pungmul.pungmul.domain.member.UserRole;
import pungmul.pungmul.dto.admin.SetRoleRequestDTO;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.repository.InstrumentStatusRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.dto.member.CreateMemberRequestDTO;
import pungmul.pungmul.dto.member.CreateAccountResponseDTO;
import pungmul.pungmul.service.file.ImageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * CreateMemberService 클래스는 회원 가입 로직을 처리하는 서비스입니다.
 * 회원 생성 요청에 따라 Account, User, InstrumentStatus 엔티티를 생성하고 저장합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CreateMemberService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final InstrumentStatusRepository instrumentStatusRepository;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;


    /**
     * 회원 생성 메서드.
     * @param createMemberRequestDto 회원 생성 요청 데이터
     * @return 회원 생성 응답 데이터
     */
    @Transactional
    public CreateAccountResponseDTO createMember(CreateMemberRequestDTO createMemberRequestDto, MultipartFile profile) throws IOException {
        //  1. account 생성
        Long accountId = createAccount(createMemberRequestDto);
        //  2. user 생성
        Long userId = createUser(createMemberRequestDto, profile, accountId);
        //  3. instrumentStatus 생성
//        createInstrument(createMemberRequestDto.getInstrumentStatusList(), userId);

        return getCreateMemberResponse(accountId, userId);
    }

    /**
     * InstrumentStatus 엔티티 생성 및 저장 메서드.
     *
     * @param instrumentStatusList 악기 상태 목록
     * @return 생성된 InstrumentStatus의 ID 리스트
     */
    public List<Long> createInstrument(Long userId, List<InstrumentStatus> instrumentStatusList) {
        ArrayList<Long> arrayList = new ArrayList<>();
        for (InstrumentStatus instrumentStatus : instrumentStatusList) {
            InstrumentStatus status = getInstrumentStatus(userId, instrumentStatus);
            instrumentStatusRepository.saveInstrument(status);
            arrayList.add(status.getId());
        }
        return arrayList;
    }

    /**
     * Account 엔티티 생성 및 저장 메서드.
     * @param createMemberRequestDto 회원 생성 요청 데이터
     * @return 생성된 Account의 ID
     */
    private Long createAccount(CreateMemberRequestDTO createMemberRequestDto) {
        Account account = getAccount(createMemberRequestDto);
        accountRepository.saveAccount(account);
        userRoleService.addRoleToAccount(getRoleRequestDTO(account));

        return account.getId();
    }

    private static SetRoleRequestDTO getRoleRequestDTO(Account account) {
        return SetRoleRequestDTO.builder()
                .username(account.getLoginId())
                .roleName(UserRole.ROLE_USER.getAuthority())
                .build();
    }

    /**
     * User 엔티티 생성 및 저장 메서드.
     * @param createMemberRequestDTO 회원 생성 요청 데이터
     * @param accountId 연결된 Account의 ID
     * @return 생성된 User의 ID
     */
    private Long createUser(CreateMemberRequestDTO createMemberRequestDTO, MultipartFile profile, Long accountId) throws IOException {
        User user = getUser(createMemberRequestDTO, accountId);
        userRepository.saveUser(user);

        imageService.saveImage(getRequestImageDTO(profile, user)
        );

//        domainImageService.saveDomainImage(DomainType.PROFILE,user.getId(), image.getId());

        return user.getId();
    }

    private static RequestImageDTO getRequestImageDTO(MultipartFile profile, User user) {
        return RequestImageDTO.builder()
                .userId(user.getId())
                .imageFile(profile)
                .domainType(DomainType.PROFILE)
                .build();
    }

    /**
     * CreateAccountResponseDTO 객체 생성 메서드.
     * @param accountId 생성된 Account 엔티티 id
     * @param userId 생성된 User 엔티티 id
     * @return 생성된 회원의 응답 데이터
     */
    private CreateAccountResponseDTO getCreateMemberResponse(Long accountId, Long userId) {
        // 계정 정보 가져오기
        Account account = accountRepository.getAccountByAccountId(accountId)
                .orElseThrow(() -> new NoSuchElementException("계정 생성 실패"));

        // 사용자 정보 가져오기 (필요한 경우)
        User user = userRepository.getUserByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자 생성 실패"));

        return CreateAccountResponseDTO.builder()
                .status("success")
                .message("회원가입이 성공적으로 완료되었습니다.")
                .data(CreateAccountResponseDTO.UserData.builder()
                        .userId(userId)
                        .loginId(account.getLoginId())
                        .userName(user.getName())
                        //.token("생성된 JWT 토큰")
                        .build())
                .redirectUrl("/login")
                .build();
    }

    private Account getAccount(CreateMemberRequestDTO createMemberRequestDto) {
        return Account.builder()
                .loginId(createMemberRequestDto.getLoginId())
                .password(passwordEncoder.encode(createMemberRequestDto.getPassword()))
                .roles(Set.of(UserRole.ROLE_USER))
                .build();
    }

    private Account getAdminAccount(CreateMemberRequestDTO createMemberRequestDto) {
        return Account.builder()
                .loginId(createMemberRequestDto.getLoginId())
                .password(passwordEncoder.encode(createMemberRequestDto.getPassword()))
                .roles(Set.of(UserRole.ROLE_ADMIN))
                .build();
    }

    /**
     * User 엔티티 생성 메서드.
     * @param createMemberRequestDto 회원 생성 요청 데이터
     * @param accountId 연결된 Account의 ID
     * @return 생성된 User 엔티티
     */
    private User getUser(CreateMemberRequestDTO createMemberRequestDto, Long accountId) {
        return User.builder()
                .accountId(accountId)
                .name(createMemberRequestDto.getName())
                .clubName(createMemberRequestDto.getClubName())
                .birth(createMemberRequestDto.getBirth())
                .email(createMemberRequestDto.getEmail())
                .phoneNumber(createMemberRequestDto.getPhoneNumber())
                .clubAge(createMemberRequestDto.getClubAge())
                .gender(createMemberRequestDto.getGender())
                .area(createMemberRequestDto.getArea())
                .clubId(createMemberRequestDto.getClubId())
                .build();
    }

    private static InstrumentStatus getInstrumentStatus(Long userId, InstrumentStatus instrumentStatus) {
        return InstrumentStatus.builder()
                .userId(userId)
                .instrument(instrumentStatus.getInstrument())
                .instrumentAbility(instrumentStatus.getInstrumentAbility())
                .major(instrumentStatus.isMajor())
                .build();
    }

}

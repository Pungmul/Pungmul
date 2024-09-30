package pungmul.pungmul.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.domain.member.instrument.InstrumentStatus;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.member.account.UserRole;
import pungmul.pungmul.dto.admin.SetRoleRequestDTO;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.dto.member.GetMemberResponseDTO;
import pungmul.pungmul.dto.member.InstrumentStatusResponseDTO;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.repository.InstrumentStatusRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.dto.member.CreateMemberRequestDTO;
import pungmul.pungmul.dto.member.CreateAccountResponseDTO;
import pungmul.pungmul.service.file.ImageService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    private final EmailService emailService;


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

        //  3. 이메일 인증 요청
//        emailService.verificationEmail(userId);

        //  email 인증 전 임시 account Enable. 이메일 인증 로직 추가 필요
        accountRepository.setEnabledAccount(accountId);

        return getCreateMemberResponse(accountId, userId);
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

    /**
     * User 엔티티 생성 및 저장 메서드.
     * @param createMemberRequestDTO 회원 생성 요청 데이터
     * @param accountId 연결된 Account의 ID
     * @return 생성된 User의 ID
     */
    private Long createUser(CreateMemberRequestDTO createMemberRequestDTO, MultipartFile profile, Long accountId) throws IOException {
        User user = getUser(createMemberRequestDTO, accountId);
        userRepository.saveUser(user);
        if (profile != null && !profile.isEmpty())
            imageService.saveImage(getRequestProfileImageDTO(profile, user));

        return user.getId();
    }

    /**
     * InstrumentStatus 엔티티 생성 및 저장 메서드.
     *
     * @param instrumentStatusList 악기 상태 목록
     * @return 생성된 InstrumentStatus의 ID 리스트
     */
    public List<Long> createInstrument(Long accountId, List<InstrumentStatus> instrumentStatusList) {
        ArrayList<Long> arrayList = new ArrayList<>();
        for (InstrumentStatus instrumentStatus : instrumentStatusList) {
            InstrumentStatus status = getInstrumentStatus(userRepository.getUserIdByAccountId(accountId), instrumentStatus);
            instrumentStatusRepository.saveInstrument(status);
            arrayList.add(status.getId());
        }
        return arrayList;
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
                .loginId(account.getLoginId())
                .userName(user.getName())
                .redirectUrl("/login-jwt")
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

    private static SetRoleRequestDTO getRoleRequestDTO(Account account) {
        return SetRoleRequestDTO.builder()
                .username(account.getLoginId())
                .roleName(UserRole.ROLE_USER.getAuthority())
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

    private static RequestImageDTO getRequestProfileImageDTO(MultipartFile profile, User user) {
        return RequestImageDTO.builder()
                .domainId(user.getId())
                .imageFile(profile)
                .userId(user.getId())
                .domainType(DomainType.PROFILE)
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

    public GetMemberResponseDTO getMemberInfo(Long accountId) {
        Account account = accountRepository.getAccountByAccountId(accountId)
                .orElseThrow(NoSuchElementException::new);
        User user = userRepository.getUserByAccountId(accountId)
                .orElseThrow(NoSuchElementException::new);
        List<InstrumentStatus> instrumentStatusList = instrumentStatusRepository.getAllInstrumentStatusByUserId(userRepository.getUserIdByAccountId(accountId))
                .orElse(Collections.emptyList() );

        return getGetMemberResponseDTO(account, user, instrumentStatusList);
    }

    private static GetMemberResponseDTO getGetMemberResponseDTO(Account account, User user, List<InstrumentStatus> instrumentStatusList) {
        return GetMemberResponseDTO.builder()
                .loginId(account.getLoginId())
                .name(user.getName())
                .clubName(user.getClubName())
                .birth(user.getBirth())
                .clubAge(user.getClubAge())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .area(user.getArea())
                .instrumentStatusDTOList(
                        instrumentStatusList.stream()
                                .map(instrumentStatus -> InstrumentStatusResponseDTO.builder()
                                        .instrument(instrumentStatus.getInstrument())
                                        .instrumentAbility(instrumentStatus.getInstrumentAbility())
                                        .major(instrumentStatus.isMajor())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }
}

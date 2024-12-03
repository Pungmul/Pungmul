package pungmul.pungmul.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.domain.member.instrument.InstrumentAbility;
import pungmul.pungmul.domain.member.instrument.InstrumentStatus;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.domain.member.account.UserRole;
import pungmul.pungmul.dto.admin.SetRoleRequestDTO;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.dto.member.*;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.repository.InstrumentStatusRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.service.file.DomainImageService;
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
    private final DomainImageService domainImageService;


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

    @Transactional
    public UpdateMemberResponseDTO updateMember(UserDetails userDetails, UpdateMemberRequestDTO updateMemberRequestDTO, MultipartFile profile) throws IOException {
        //  1. password 수정
        if (!updateMemberRequestDTO.getPassword().isEmpty())
            updatePassword(userDetails, updateMemberRequestDTO.getPassword());

        //  2. user 수정
        updateUserInfo(userDetails, updateMemberRequestDTO);

        //  3. profile 수정
        if (!profile.isEmpty())
            updateProfileImage(userDetails, profile);

        return getUpdateMemberResponseDTO(userDetails);
    }

    public UpdateMemberResponseDTO getUpdateMemberResponseDTO(UserDetails userDetails) {
        User user = userRepository.getUserByEmail(userDetails.getUsername())
                .orElseThrow(NoSuchElementException::new);

        return UpdateMemberResponseDTO.builder()
                .name(user.getName())
                .clubName(user.getClubName())
                .phoneNumber(user.getPhoneNumber())
                .area(user.getArea())
                .clubId(user.getClubId())
                .updateAt(user.getUpdatedAt())
                .build();
    }

    private void updateProfileImage(UserDetails userDetails, MultipartFile profile) throws IOException {
        User user = userRepository.getUserByEmail(userDetails.getUsername())
                .orElseThrow(NoSuchElementException::new);

        if (profile != null && !profile.isEmpty()) {
            RequestImageDTO requestProfileImageDTO = getRequestProfileImageDTO(profile, user);
            Long imageId = imageService.saveImage(requestProfileImageDTO);
            updateProfileImage(user.getId(), imageId);
        }
    }

    private void updateProfileImage(Long userId, Long imageId) {
        domainImageService.updatePrimaryImage(DomainType.PROFILE,userId, imageId);
    }

    private void updateUserInfo(UserDetails userDetails, UpdateMemberRequestDTO updateMemberRequestDTO) {
        Long userId = userRepository.getUserByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new UsernameNotFoundException(userDetails.getUsername()));
        User updateUser = getUpdateUser(userId, updateMemberRequestDTO);
        log.info(updateUser.getPhoneNumber());
        userRepository.updateUser(updateUser);
    }

    private static User getUpdateUser(Long userId, UpdateMemberRequestDTO updateMemberRequestDTO) {
        return User.builder()
                .id(userId)
                .phoneNumber(updateMemberRequestDTO.getPhoneNumber())
                .area(updateMemberRequestDTO.getArea())
                .clubId(updateMemberRequestDTO.getClubId())
                .clubName(updateMemberRequestDTO.getClubName())
                .build();
    }

    private void updatePassword(UserDetails userDetails, String password) throws IOException {
        Long accountId = accountRepository.getAccountByLoginId(userDetails.getUsername())
                .map(Account::getId)
                .orElseThrow(() -> new UsernameNotFoundException(userDetails.getUsername()));
        accountRepository.updatePassword(accountId, password);
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

        log.info("user auth : {}", account.getRoles());

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

    private SetRoleRequestDTO getRoleRequestDTO(Account account) {
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

    public String getUserStatus(Long accountId) {
        Optional<Account> accountOptional = accountRepository.getAccountByAccountId(accountId);

        if (accountOptional.isEmpty())
            return "존재하지 않는 사용자";

        Account account = accountOptional.get();
        if (account.isWithdraw())
            return "탈퇴한 사용자";

        return "정상 사용자";
    }



    private RequestImageDTO getRequestProfileImageDTO(MultipartFile profile, User user) {
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

    @Transactional
    public UpdateInstrumentResponseDTO updateInstrumentStatus(UserDetails userDetails, UpdateInstrumentRequestDTO updateInstrumentRequestDTO) {
        User user = userRepository.getUserByEmail(userDetails.getUsername())
                .orElseThrow(NoSuchElementException::new);

        InstrumentStatus instrumentStatus = getUpdateInstrumentStatus(user, updateInstrumentRequestDTO);
//        instrumentStatusRepository.updateInstrumentStatus(instrumentStatus);

        instrumentStatusRepository.setMajorFalseForOtherInstruments(instrumentStatus);
        instrumentStatusRepository.updateInstrumentAbilityAndMajor(instrumentStatus);

        return UpdateInstrumentResponseDTO.builder()
                .instruments(instrumentStatusRepository.getAllInstrumentStatusByUserId(user.getId())
                        .orElseThrow(NoSuchElementException::new))
                .build();
    }

    @Transactional
    public void deleteUser(UserDetails userDetails){
        accountRepository.deleteAccount(userDetails.getUsername());
        userRepository.deleteUser(userDetails.getUsername());
    }

    private static InstrumentStatus getUpdateInstrumentStatus(User user, UpdateInstrumentRequestDTO updateInstrumentRequestDTO) {
        return InstrumentStatus.builder()
                .userId(user.getId())
                .instrument(updateInstrumentRequestDTO.getInstrument())
                .instrumentAbility(updateInstrumentRequestDTO.getInstrumentAbility())
                .major(updateInstrumentRequestDTO.getMajor())
                .build();
    }
}

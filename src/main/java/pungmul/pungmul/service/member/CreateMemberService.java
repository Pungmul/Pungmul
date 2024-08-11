package pungmul.pungmul.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.InstrumentStatus;
import pungmul.pungmul.domain.member.User;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.repository.InstrumentStatusRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.dto.member.CreateAccountRequestDTO;
import pungmul.pungmul.dto.member.CreateAccountResponseDTO;

import java.util.ArrayList;
import java.util.List;

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
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 생성 메서드.
     * @param createAccountRequestDto 회원 생성 요청 데이터
     * @return 회원 생성 응답 데이터
     */
    @Transactional
    public CreateAccountResponseDTO createMember(CreateAccountRequestDTO createAccountRequestDto) {
        Long accountId = createAccount(createAccountRequestDto);
        Long userId = createUser(createAccountRequestDto, accountId);
        createInstrument(createAccountRequestDto.getInstrumentStatusList(), userId);

        Account account = accountRepository.getAccountByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Account 생성 실패"));

        User user = userRepository.getUserByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User 생성 실패"));

        return getCreateAccountResponse(account, user);
    }

    /**
     * Account 엔티티 생성 및 저장 메서드.
     * @param createAccountRequestDto 회원 생성 요청 데이터
     * @return 생성된 Account의 ID
     */
    private Long createAccount(CreateAccountRequestDTO createAccountRequestDto) {
        Account account = Account.builder()
                .loginId(createAccountRequestDto.getLoginId())
                .password(passwordEncoder.encode(createAccountRequestDto.getPassword()))
                .build();
        accountRepository.saveAccount(account);
        return account.getId();
    }

    /**
     * User 엔티티 생성 및 저장 메서드.
     * @param createAccountRequestDto 회원 생성 요청 데이터
     * @param accountId 연결된 Account의 ID
     * @return 생성된 User의 ID
     */
    private Long createUser(CreateAccountRequestDTO createAccountRequestDto, Long accountId) {
        User user = getUser(createAccountRequestDto, accountId);
        userRepository.saveUser(user);
        return user.getId();
    }

    /**
     * InstrumentStatus 엔티티 생성 및 저장 메서드.
     * @param instrumentStatusList 악기 상태 목록
     * @param userId 연결된 User의 ID
     * @return 생성된 InstrumentStatus의 ID 리스트
     */
    private List<Long> createInstrument(List<InstrumentStatus> instrumentStatusList, Long userId) {
        ArrayList<Long> arrayList = new ArrayList<>();
        for (InstrumentStatus instrumentStatus : instrumentStatusList) {
            InstrumentStatus status = InstrumentStatus.builder()
                    .userId(userId)
                    .instrument(instrumentStatus.getInstrument())
                    .instrumentAbility(instrumentStatus.getInstrumentAbility())
                    .major(instrumentStatus.isMajor())
                    .build();
            instrumentStatusRepository.saveInstrument(status);
            arrayList.add(status.getId());
        }
        return arrayList;
    }

    /**
     * CreateAccountResponseDTO 객체 생성 메서드.
     * @param account 생성된 Account 엔티티
     * @param user 생성된 User 엔티티
     * @return 생성된 회원의 응답 데이터
     */
    private CreateAccountResponseDTO getCreateAccountResponse(Account account, User user) {
        return CreateAccountResponseDTO.builder()
                .status("success")
                .message("회원가입이 성공적으로 완료되었습니다.")
                .data(CreateAccountResponseDTO.UserData.builder()
                        .userId(account.getId())
                        .loginId(account.getLoginId())
                        .userName(user.getName())
//                        .token("생성된 JWT 토큰")
                        .build())
                .redirectUrl("/login")
                .build();
    }

    /**
     * User 엔티티 생성 메서드.
     * @param createAccountRequestDto 회원 생성 요청 데이터
     * @param accountId 연결된 Account의 ID
     * @return 생성된 User 엔티티
     */
    private User getUser(CreateAccountRequestDTO createAccountRequestDto, Long accountId) {
        return User.builder()
                .accountId(accountId)
                .name(createAccountRequestDto.getName())
                .clubName(createAccountRequestDto.getClubName())
                .birth(createAccountRequestDto.getBirth())
                .email(createAccountRequestDto.getEmail())
                .phoneNumber(createAccountRequestDto.getPhoneNumber())
                .clubAge(createAccountRequestDto.getClubAge())
                .gender(createAccountRequestDto.getGender())
                .area(createAccountRequestDto.getArea())
                .clubId(createAccountRequestDto.getClubId())
                .build();
    }
}

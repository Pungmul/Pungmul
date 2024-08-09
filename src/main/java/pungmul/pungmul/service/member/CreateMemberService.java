package pungmul.pungmul.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.InstrumentStatus;
import pungmul.pungmul.domain.member.User;
import pungmul.pungmul.repository.member.base.repository.AccountRepository;
import pungmul.pungmul.repository.member.base.repository.InstrumentStatusRepository;
import pungmul.pungmul.repository.member.base.repository.UserRepository;
import pungmul.pungmul.repository.member.signup.CreateMemberRepository;
import pungmul.pungmul.web.member.dto.CreateAccountRequestDTO;
import pungmul.pungmul.web.member.dto.CreateAccountResponseDTO;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CreateMemberService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final InstrumentStatusRepository instrumentStatusRepository;
    private final PasswordEncoder passwordEncoder;

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

    private Long createAccount(CreateAccountRequestDTO createAccountRequestDto) {
        Account account = Account.builder()
                .loginId(createAccountRequestDto.getLoginId())
                .password(passwordEncoder.encode(createAccountRequestDto.getPassword()))
                .build();
        accountRepository.saveAccount(account);
        return account.getId();
    }

    private Long createUser(CreateAccountRequestDTO createAccountRequestDto, Long accountId) {
        User user = getUser(createAccountRequestDto, accountId);
        userRepository.saveUser(user);
        return user.getId();
    }

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

    private CreateAccountResponseDTO getCreateAccountResponse(Account account, User user) {
        return CreateAccountResponseDTO.builder()
                .status("success")
                .message("회원가입이 성공적으로 완료되었습니다.")
                .data(CreateAccountResponseDTO.UserData.builder()
                        .userId(account.getId())
                        .username(account.getLoginId())
                        .email(user.getEmail())
                        .token("생성된 JWT 토큰")
                        .build())
                .redirectUrl("/login")
                .build();
    }

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

package pungmul.pungmul.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.InstrumentStatus;
import pungmul.pungmul.domain.member.LoginForm;
import pungmul.pungmul.domain.member.User;
import pungmul.pungmul.repository.member.CreateMemberRepository;
import pungmul.pungmul.repository.member.MybatisCreateMemberRepository;
import pungmul.pungmul.web.member.dto.CreateAccountRequestDto;
import pungmul.pungmul.web.member.dto.CreateAccountResponseDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CreateMemberService {
    private final CreateMemberRepository createMemberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CreateAccountResponseDto createMember(CreateAccountRequestDto createAccountRequestDto) {
        Account account = createAccount(createAccountRequestDto);
        log.info("ID : {}" ,  account.getId());
        User user = createUser(createAccountRequestDto, account.getId());
        log.info("ID : {}" , user.getId());
        createInstrument(createAccountRequestDto.getInstrumentStatusList(), user.getId());

        return getCreateAccountResponse(account, user);
    }

    private void createInstrument(List<InstrumentStatus> instrumentStatusList, Long userId) {
        for (InstrumentStatus instrumentStatus : instrumentStatusList) {
            InstrumentStatus status = InstrumentStatus.builder()
                    .userId(userId)
                    .instrument(instrumentStatus.getInstrument())
                    .instrumentAbility(instrumentStatus.getInstrumentAbility())
                    .major(instrumentStatus.isMajor())
                    .build();
            createMemberRepository.saveInstrument(status);
        }
    }

    private static CreateAccountResponseDto getCreateAccountResponse(Account account, User user) {
        return CreateAccountResponseDto.builder()
                .status("success")
                .message("회원가입이 성공적으로 완료되었습니다.")
                .data(CreateAccountResponseDto.UserData.builder()
                        .userId(account.getId())
                        .username(account.getLoginId())
                        .email(user.getEmail())
                        .token("생성된 JWT 토큰")
                        .build())
                .redirectUrl("/login")
                .build();
    }

    private Account createAccount(CreateAccountRequestDto createAccountRequestDto) {
        Account account = Account.builder()
                .loginId(createAccountRequestDto.getLoginId())
                .password(passwordEncoder.encode(createAccountRequestDto.getPassword()))
                .build();
        return createMemberRepository.saveAccount(account);
    }

    private User createUser(CreateAccountRequestDto createAccountRequestDto, Long accountId) {
        return createMemberRepository.saveUser(getUser(createAccountRequestDto, accountId));

    }

    private static User getUser(CreateAccountRequestDto createAccountRequestDto, Long accountId) {
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

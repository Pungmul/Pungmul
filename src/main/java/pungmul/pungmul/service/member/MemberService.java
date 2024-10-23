package pungmul.pungmul.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.domain.member.instrument.InstrumentStatus;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.dto.member.SimpleUserListResponseDTO;
import pungmul.pungmul.dto.member.GetMemberResponseDTO;
import pungmul.pungmul.dto.member.InstrumentStatusResponseDTO;
import pungmul.pungmul.dto.member.SimpleUserDTO;
import pungmul.pungmul.repository.image.repository.ImageRepository;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.repository.InstrumentStatusRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.service.file.ImageService;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final InstrumentStatusRepository instrumentStatusRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;

    public SimpleUserDTO getSimpleUserDTO(Long userId){
        return userRepository.getUserByUserId(userId)
                    .map(this::buildSimpleUserDTO)
                    .orElseThrow(NoSuchElementException::new);
    }

    private SimpleUserDTO buildSimpleUserDTO(User user){
//        log.info("user name : {}", user.getName());
        Image profile = imageRepository.getImagesByDomainIdAndType(DomainType.PROFILE, user.getId()).stream().findFirst().orElseGet(imageService::getAnonymousImage);

        return SimpleUserDTO.builder()
                    .userId(user.getId())
                    .username(user.getEmail())
                    .name(user.getName())
                    .profileImage(profile)
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

//    public SimpleUserListResponseDTO searchUsers(String keyword) {
//        List<User> users = userRepository.searchUsersByKeyword(keyword);
//
//        return (SimpleUserListResponseDTO) users.stream()
//                .map(user -> SimpleUserDTO.builder()
//                        .userId(user.getId())
//                        .username(user.getEmail())
//                        .name(user.getName())
//                        .profileImage(
//                                imageRepository.getImagesByDomainIdAndType(DomainType.PROFILE, user.getId()).stream().findFirst().orElseGet(imageService::getAnonymousImage)
//                        )
//                        .build())
//                .collect(Collectors.toList());
//    }

    public SimpleUserListResponseDTO searchUsers(String keyword) {
        List<User> users = userRepository.searchUsersByKeyword(keyword);

        // 사용자 리스트를 SimpleUserDTO 리스트로 변환
        List<SimpleUserDTO> simpleUserDTOList = users.stream()
                .map(user -> SimpleUserDTO.builder()
                        .userId(user.getId())
                        .username(user.getEmail())
                        .name(user.getName())
                        .profileImage(
                                imageRepository.getImagesByDomainIdAndType(DomainType.PROFILE, user.getId()).stream().findFirst().orElseGet(imageService::getAnonymousImage)
                        )
                        .build())
                .collect(Collectors.toList());

        // SimpleUserListResponseDTO에 변환된 리스트를 담아 반환
        return SimpleUserListResponseDTO.builder()
                .simpleUserDTOList(simpleUserDTOList)  // 필드명을 적절하게 사용하세요
                .build();
    }

}

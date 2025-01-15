package pungmul.pungmul.service.member.membermanagement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.domain.file.Image;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.domain.member.instrument.InstrumentStatus;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.dto.member.*;
import pungmul.pungmul.repository.image.repository.ImageRepository;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.repository.ClubRepository;
import pungmul.pungmul.repository.member.repository.InstrumentStatusRepository;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.service.file.ImageService;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    private final UserRepository userRepository;
    private final InstrumentStatusRepository instrumentStatusRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final ClubRepository clubRepository;
    private final AccountService accountService;

    public SimpleUserDTO getSimpleUserDTO(Long userId){
        log.info("userId : {}", userId);
        return userRepository.getUserByUserId(userId)
                    .map(this::buildSimpleUserDTO)
                    .orElseThrow(NoSuchElementException::new);
    }

    private SimpleUserDTO buildSimpleUserDTO(User user){
        log.info("user name : {}", user.getName());
        Image profile = imageRepository.getImagesByDomainIdAndType(DomainType.PROFILE, user.getId()).stream().findFirst().orElseGet(imageService::getAnonymousImage);

        return SimpleUserDTO.builder()
                    .userId(user.getId())
                    .username(user.getEmail())
                    .name(user.getName())
                    .profileImage(profile)
                    .build();
    }

    public GetMemberResponseDTO getMemberInfo(String username) {
        User user = userRepository.getUserByEmail(username)
                .orElseThrow(NoSuchElementException::new);
        List<InstrumentStatus> instrumentStatusList = instrumentStatusRepository.getAllInstrumentStatusByUserId(userRepository.getUserByEmail(username).map(User::getId).orElseThrow(NoSuchElementException::new))
                .orElse(Collections.emptyList());

        return getGetMemberResponseDTO(username, user, instrumentStatusList);
    }

    public GetMemberResponseDTO getGetMemberResponseDTO(String username, User user, List<InstrumentStatus> instrumentStatusList) {
        return GetMemberResponseDTO.builder()
                .username(username)
                .name(user.getName())
                .clubName(user.getClubName())
                .groupName(clubRepository.getGroupName(user.getClubId()))
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .userRole(accountService.getAccountByEmail(username).getRoles().stream().findFirst().orElse(null))
                .profile(imageService.getImagesByDomainId(DomainType.PROFILE, user.getId()).get(0))
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

    public SearchUserResponseDTO searchUsers(UserDetails userDetails, String keyword) {
        List<SimpleUserDTO> users = userRepository.searchUsersByKeyword(keyword).stream()
                .filter(user -> !user.getEmail().equals(userDetails.getUsername()))
                .map(user -> SimpleUserDTO.builder()
                        .username(user.getEmail())
                        .userId(user.getId())
                        .name(user.getName())
                        .profileImage(imageRepository.getImagesByDomainIdAndType(DomainType.PROFILE, user.getId())
                                .stream().findFirst().orElseGet(imageService::getAnonymousImage))
                        .build())  // 빌더의 완료
                .toList();

        return SearchUserResponseDTO.builder()
                .users(users)
                .build();
    }
}

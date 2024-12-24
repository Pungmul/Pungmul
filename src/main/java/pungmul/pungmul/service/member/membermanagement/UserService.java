package pungmul.pungmul.service.member.membermanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.dto.member.CreateMemberRequestDTO;
import pungmul.pungmul.dto.member.UpdateMemberRequestDTO;
import pungmul.pungmul.dto.member.UpdateMemberResponseDTO;
import pungmul.pungmul.repository.member.repository.UserRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Long createUser(CreateMemberRequestDTO createMemberRequestDTO, Long accountId) {
        User user = User.builder()
                .accountId(accountId)
                .name(createMemberRequestDTO.getName())
                .clubName(createMemberRequestDTO.getClubName())
//                .birth(createMemberRequestDTO.getBirth())
                .email(createMemberRequestDTO.getEmail())
                .phoneNumber(createMemberRequestDTO.getPhoneNumber())
//                .clubAge(createMemberRequestDTO.getClubAge())
//                .gender(createMemberRequestDTO.getGender())
//                .area(createMemberRequestDTO.getArea())
                .clubId(createMemberRequestDTO.getClubId())
                .build();
        userRepository.saveUser(user);
        return user.getId();
    }

    public void updateUserInfo(String email, UpdateMemberRequestDTO updateMemberRequestDTO) {
        Long userId = userRepository.getUserByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        User updatedUser = User.builder()
                .id(userId)
                .phoneNumber(updateMemberRequestDTO.getPhoneNumber())
//                .area(updateMemberRequestDTO.getArea())
                .clubId(updateMemberRequestDTO.getClubId())
                .clubName(updateMemberRequestDTO.getClubName())
                .build();

        userRepository.updateUser(updatedUser);
    }

    public void deleteUser(String email) {
        userRepository.deleteUser(email);
    }

    public UpdateMemberResponseDTO getUpdateMemberResponse(String email) {
        User user = userRepository.getUserByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return UpdateMemberResponseDTO.builder()
                .name(user.getName())
                .clubName(user.getClubName())
                .phoneNumber(user.getPhoneNumber())
//                .area(user.getArea())
                .clubId(user.getClubId())
                .updateAt(user.getUpdatedAt())
                .build();
    }

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email)
                .orElseThrow(NoSuchElementException::new);
    }

    public void banUser(String username) {
        userRepository.banUser(username);
    }
}

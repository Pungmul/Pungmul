package pungmul.pungmul.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.UserRole;
import pungmul.pungmul.dto.admin.SetRoleRequestDTO;
import pungmul.pungmul.dto.admin.SetRoleResponseDTO;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.repository.UserRoleRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;
    private final AccountRepository accountRepository;

//    public void insertRole(AddRoleRequestDTO addRoleRequestDTO) {
//        userRoleRepository.insertRole(role);
//    }

    public SetRoleResponseDTO addRoleToAccount(SetRoleRequestDTO setRoleRequestDTO) {
        String username = setRoleRequestDTO.getUsername();
        String roleName = setRoleRequestDTO.getRoleName();

        Long accountId = accountRepository.getAccountByLoginId(username)
                .map(Account::getId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for username: " + username));
        userRoleRepository.addRoleToAccount(accountId, roleName);

        return SetRoleResponseDTO.builder()
                .username(username)
                .roleName(roleName)
                .build();
    }

    public Set<UserRole> getRolesByAccountId(Long accountId) {
        return userRoleRepository.getRolesByAccountId(accountId);
    }

    public void removeRoleFromAccount(Long accountId, Long roleId) {
        userRoleRepository.removeRoleFromAccount(accountId, roleId);
    }

    public void removeAllRolesFromAccount(Long accountId) {
        userRoleRepository.removeAllRolesFromAccount(accountId);
    }
}

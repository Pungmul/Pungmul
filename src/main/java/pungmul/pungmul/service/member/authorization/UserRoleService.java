package pungmul.pungmul.service.member.authorization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.member.account.Account;
import pungmul.pungmul.domain.member.account.UserRole;
import pungmul.pungmul.dto.admin.SetRoleRequestDTO;
import pungmul.pungmul.dto.admin.SetRoleResponseDTO;
import pungmul.pungmul.repository.member.repository.AccountRepository;
import pungmul.pungmul.repository.member.repository.UserRoleRepository;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;
    private final AccountRepository accountRepository;

//    public void insertRole(AddRoleRequestDTO addRoleRequestDTO) {
//        userRoleRepository.insertRole(role);
//    }

    @Transactional
    public SetRoleResponseDTO addRoleToAccount(SetRoleRequestDTO setRoleRequestDTO) {
        String username = setRoleRequestDTO.getUsername();
        String roleName = setRoleRequestDTO.getRoleName();

        Long accountId = accountRepository.getAccountByUsername(username)
                .map(Account::getId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for username: " + username));
        userRoleRepository.addRoleToAccount(accountId, roleName);
        log.info("add user role : {}", userRoleRepository.getRolesByAccountId(accountId));

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

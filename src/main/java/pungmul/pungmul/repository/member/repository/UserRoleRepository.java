package pungmul.pungmul.repository.member.repository;

import pungmul.pungmul.domain.member.account.UserRole;

import java.util.Set;

public interface UserRoleRepository {
    void insertRole(UserRole userRole);

    void addRoleToAccount(Long accountId, String role);

    Set<UserRole> getRolesByAccountId(Long accountId);

    void removeRoleFromAccount(Long accountId, Long roleId);

    void removeAllRolesFromAccount(Long accountId);
}

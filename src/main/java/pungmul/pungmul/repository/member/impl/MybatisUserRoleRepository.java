package pungmul.pungmul.repository.member.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pungmul.pungmul.domain.member.UserRole;
import pungmul.pungmul.repository.member.mapper.UserRoleMapper;
import pungmul.pungmul.repository.member.repository.UserRoleRepository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class MybatisUserRoleRepository implements UserRoleRepository {
    private final UserRoleMapper userRoleMapper;

    @Override
    public void insertRole(UserRole userRole) {
        userRoleMapper.insertRole(userRole);
    }

    @Override
    public void addRoleToAccount(Long accountId, String role) {
        userRoleMapper.addRoleToAccount(accountId, role);
    }

    @Override
    public Set<UserRole> getRolesByAccountId(Long accountId) {
        return userRoleMapper.getRolesByAccountId(accountId);
    }

    @Override
    public void removeRoleFromAccount(Long accountId, Long roleId) {
        userRoleMapper.removeRoleFromAccount(accountId, roleId);
    }

    @Override
    public void removeAllRolesFromAccount(Long accountId) {
        userRoleMapper.removeAllRolesFromAccount(accountId);
    }
}

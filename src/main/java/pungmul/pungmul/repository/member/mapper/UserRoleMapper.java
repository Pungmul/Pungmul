package pungmul.pungmul.repository.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pungmul.pungmul.domain.member.UserRole;

import java.util.List;
import java.util.Set;

@Mapper
public interface UserRoleMapper {
    void insertRole(UserRole role);

    void addRoleToAccount(@Param("accountId") Long accountId,@Param("role") String role);

    Set<UserRole> getRolesByAccountId(Long accountId);

    void removeRoleFromAccount(Long accountId, Long roleId);

    void removeAllRolesFromAccount(Long accountId);
}

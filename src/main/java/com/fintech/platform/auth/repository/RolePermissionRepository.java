package com.fintech.platform.auth.repository;

import com.fintech.platform.auth.entity.Permission;
import com.fintech.platform.auth.entity.Role;
import com.fintech.platform.auth.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRole(Role role);
    boolean existsByRoleAndPermission(Role role, Permission permission);
}
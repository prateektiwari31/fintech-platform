package com.fintech.platform.auth.repository;

import com.fintech.platform.auth.entity.UserRole;
import com.fintech.platform.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUser(User user);
}
package com.fintech.platform.config;

import com.fintech.platform.auth.entity.Permission;
import com.fintech.platform.auth.entity.Role;
import com.fintech.platform.auth.entity.RolePermission;
import com.fintech.platform.auth.repository.PermissionRepository;
import com.fintech.platform.auth.repository.RolePermissionRepository;
import com.fintech.platform.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public void run(String... args) {

        // ---------- Roles ----------
        createRole("ADMIN", "System Administrator");
        createRole("CUSTOMER", "Application Customer");
        createRole("SUPPORT", "Support Executive");

        // ---------- Permissions ----------
        createPermission("BOOK_FLIGHT", "Book Flight");
        createPermission("BOOK_TRAIN", "Book Train");
        createPermission("VIEW_BOOKING", "View Booking");
        createPermission("CANCEL_BOOKING", "Cancel Booking");
        createPermission("REFUND", "Refund Booking");
        createPermission("MANAGE_USERS", "Manage Users");

        // ---------- Mappings ----------
        map("ADMIN", "BOOK_FLIGHT");
        map("ADMIN", "BOOK_TRAIN");
        map("ADMIN", "VIEW_BOOKING");
        map("ADMIN", "CANCEL_BOOKING");
        map("ADMIN", "REFUND");
        map("ADMIN", "MANAGE_USERS");

        map("CUSTOMER", "BOOK_FLIGHT");
        map("CUSTOMER", "BOOK_TRAIN");
        map("CUSTOMER", "VIEW_BOOKING");
        map("CUSTOMER", "CANCEL_BOOKING");

        map("SUPPORT", "VIEW_BOOKING");
        map("SUPPORT", "REFUND");
    }

    private void createRole(String name, String description) {

        if (!roleRepository.existsByName(name)) {

            roleRepository.save(
                    Role.builder()
                            .name(name)
                            .description(description)
                            .build()
            );
        }
    }

    private void createPermission(String name, String description) {

        if (!permissionRepository.existsByName(name)) {

            permissionRepository.save(
                    Permission.builder()
                            .name(name)
                            .description(description)
                            .build()
            );
        }
    }

    private void map(String roleName, String permissionName) {

        Role role = roleRepository.findByName(roleName)
                .orElseThrow();

        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow();

        if (!rolePermissionRepository.existsByRoleAndPermission(role, permission)) {

            rolePermissionRepository.save(
                    RolePermission.builder()
                            .role(role)
                            .permission(permission)
                            .build()
            );
        }
    }
}
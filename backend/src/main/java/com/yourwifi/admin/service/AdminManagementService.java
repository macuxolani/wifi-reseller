package com.yourwifi.admin.service;

import com.yourwifi.admin.dto.AdminResponse;
import com.yourwifi.admin.dto.CreateAdminRequest;
import com.yourwifi.admin.entity.AdminUser;
import com.yourwifi.admin.entity.Role;
import com.yourwifi.admin.repository.AdminUserRepository;
import com.yourwifi.admin.repository.RoleRepository;
import com.yourwifi.common.enums.UserStatus;
import com.yourwifi.common.exception.ApiException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminManagementService {
    private final AdminUserRepository adminUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminManagementService(AdminUserRepository adminUserRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<AdminResponse> list() {
        return adminUserRepository.findAll().stream().map(this::response).toList();
    }

    @Transactional
    public AdminResponse create(CreateAdminRequest request) {
        if (adminUserRepository.findByUsername(request.username()).isPresent()) {
            throw new ApiException("ADMIN_EXISTS", "Admin username already exists.", HttpStatus.CONFLICT.value());
        }
        AdminUser admin = new AdminUser();
        admin.setId(UUID.randomUUID());
        admin.setUsername(request.username());
        admin.setEmail(request.email());
        admin.setPasswordHash(passwordEncoder.encode(request.password()));
        admin.setStatus(UserStatus.ACTIVE);
        admin.setCreatedAt(Instant.now());
        admin.setRoles(request.roles().stream().map(role -> roleRepository.findByName(role)
            .orElseThrow(() -> new ApiException("ROLE_NOT_FOUND", "Role not found: " + role, 400)))
            .collect(Collectors.toSet()));
        return response(adminUserRepository.save(admin));
    }

    @Transactional
    public void remove(UUID id) {
        AdminUser admin = adminUserRepository.findById(id)
            .orElseThrow(() -> new ApiException("ADMIN_NOT_FOUND", "Admin not found.", 404));
        if ("admin".equals(admin.getUsername())) {
            throw new ApiException("PROTECTED_ADMIN", "The seeded super admin cannot be removed.", 400);
        }
        adminUserRepository.delete(admin);
    }

    private AdminResponse response(AdminUser admin) {
        return new AdminResponse(admin.getId(), admin.getUsername(), admin.getEmail(), admin.getStatus(),
            admin.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
    }
}
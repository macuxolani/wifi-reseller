package com.yourwifi.admin.repository;

import com.yourwifi.admin.entity.Role;
import com.yourwifi.common.enums.RoleName;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(RoleName name);
}
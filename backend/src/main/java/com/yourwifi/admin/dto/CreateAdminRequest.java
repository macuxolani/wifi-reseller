package com.yourwifi.admin.dto;

import com.yourwifi.common.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record CreateAdminRequest(
    @NotBlank String username,
    @NotBlank @Email String email,
    @NotBlank String password,
    @NotEmpty Set<RoleName> roles
) {}
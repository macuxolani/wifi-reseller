package com.yourwifi.admin.dto;

import com.yourwifi.common.enums.RoleName;
import com.yourwifi.common.enums.UserStatus;
import java.util.Set;
import java.util.UUID;

public record AdminResponse(UUID id, String username, String email, UserStatus status, Set<RoleName> roles) {}
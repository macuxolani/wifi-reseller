package com.yourwifi.admin.service;

import com.yourwifi.admin.dto.AdminOverviewResponse;
import com.yourwifi.admin.repository.AdminUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminUserRepository adminUserRepository;

    public AdminService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    public AdminOverviewResponse getOverview() {
        long totalAdmins = adminUserRepository.count();
        return new AdminOverviewResponse(totalAdmins, 5L, 0L);
    }
}

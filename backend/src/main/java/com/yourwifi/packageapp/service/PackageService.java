package com.yourwifi.packageapp.service;

import com.yourwifi.common.enums.PackageStatus;
import com.yourwifi.packageapp.dto.PackageDto;
import com.yourwifi.packageapp.entity.Package;
import com.yourwifi.packageapp.repository.PackageRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PackageService {

    private final PackageRepository packageRepository;

    public PackageService(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    public List<PackageDto> listActivePackages() {
        return packageRepository.findByStatus(PackageStatus.ACTIVE).stream()
            .map(pkg -> new PackageDto(
                pkg.getId(),
                pkg.getName(),
                pkg.getDurationMinutes(),
                pkg.getDownloadSpeedMbps(),
                pkg.getUploadSpeedMbps(),
                pkg.getPrice(),
                pkg.getCurrency(),
                pkg.getDescription(),
                pkg.getStatus().name()
            ))
            .toList();
    }
}

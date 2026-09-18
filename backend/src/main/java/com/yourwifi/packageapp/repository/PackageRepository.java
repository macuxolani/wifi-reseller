package com.yourwifi.packageapp.repository;

import com.yourwifi.common.enums.PackageStatus;
import com.yourwifi.packageapp.entity.Package;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<Package, UUID> {
    List<Package> findByStatus(PackageStatus status);
}

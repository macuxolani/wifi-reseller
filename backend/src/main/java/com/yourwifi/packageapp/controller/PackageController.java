package com.yourwifi.packageapp.controller;

import com.yourwifi.packageapp.dto.PackageDto;
import com.yourwifi.packageapp.service.PackageService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping("/packages")
    public ResponseEntity<List<PackageDto>> listPackages() {
        return ResponseEntity.ok(packageService.listActivePackages());
    }
}

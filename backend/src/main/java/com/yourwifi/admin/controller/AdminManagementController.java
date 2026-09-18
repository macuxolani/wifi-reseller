package com.yourwifi.admin.controller;

import com.yourwifi.admin.dto.AdminResponse;
import com.yourwifi.admin.dto.CreateAdminRequest;
import com.yourwifi.admin.service.AdminManagementService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
public class AdminManagementController {
    private final AdminManagementService service;

    public AdminManagementController(AdminManagementService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AdminResponse>> list() { return ResponseEntity.ok(service.list()); }

    @PostMapping
    public ResponseEntity<AdminResponse> create(@Valid @RequestBody CreateAdminRequest request) { return ResponseEntity.ok(service.create(request)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable UUID id) { service.remove(id); return ResponseEntity.noContent().build(); }
}
package com.saludlink.presentation.controller;

import com.saludlink.application.dto.AdminDoctorCreateDTO;
import com.saludlink.application.dto.DoctorResponseDTO;
import com.saludlink.application.service.AdminDoctorService;
import com.saludlink.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/doctors")
@RequiredArgsConstructor
public class AdminDoctorController {

    private final AdminDoctorService adminDoctorService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DoctorResponseDTO>> listMyDoctors(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(adminDoctorService.listDoctorsByAdmin(principal.getUser().getId()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponseDTO> registerDoctor(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody AdminDoctorCreateDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminDoctorService.registerDoctor(principal.getUser().getId(), dto));
    }
}
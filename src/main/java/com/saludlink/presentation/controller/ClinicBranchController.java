package com.saludlink.presentation.controller;

import com.saludlink.application.dto.ClinicBranchRequestDTO;
import com.saludlink.application.dto.ClinicBranchResponseDTO;
import com.saludlink.domain.model.entity.ClinicBranch;
import com.saludlink.infrastructure.persistence.repository.ClinicBranchRepository;
import com.saludlink.infrastructure.persistence.repository.ClinicRepository;
import com.saludlink.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
public class ClinicBranchController {

    private final ClinicRepository clinicRepository;
    private final ClinicBranchRepository clinicBranchRepository;

    @GetMapping("/{clinicId}/branches")
    public ResponseEntity<List<ClinicBranchResponseDTO>> listByClinic(@PathVariable Long clinicId) {
        List<ClinicBranchResponseDTO> list =
                clinicBranchRepository.findByClinicIdAndActiveTrueOrderByNameAsc(clinicId).stream()
                        .map(ClinicBranchResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/me/branches")
    public ResponseEntity<List<ClinicBranchResponseDTO>> myBranches(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        var clinic =
                clinicRepository
                        .findByAdminUserId(principal.getUser().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clínica no encontrada"));

        List<ClinicBranchResponseDTO> list =
                clinicBranchRepository.findByClinicIdAndActiveTrueOrderByNameAsc(clinic.getId()).stream()
                        .map(ClinicBranchResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(list);
    }

    @PostMapping("/me/branches")
    @ResponseStatus(HttpStatus.CREATED)
    public ClinicBranchResponseDTO createBranch(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ClinicBranchRequestDTO dto
    ) {
        var clinic =
                clinicRepository
                        .findByAdminUserId(principal.getUser().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clínica no encontrada"));

        ClinicBranch branch =
                ClinicBranch.builder()
                        .clinic(clinic)
                        .name(dto.getName())
                        .address(dto.getAddress())
                        .ruc(dto.getRuc())
                        .active(true)
                        .build();

        return ClinicBranchResponseDTO.fromEntity(clinicBranchRepository.save(branch));
    }
}
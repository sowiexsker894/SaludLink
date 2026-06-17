package com.saludlink.presentation.controller;

import com.saludlink.application.dto.ClinicResponseDTO;
import com.saludlink.infrastructure.persistence.repository.ClinicRepository;
import com.saludlink.infrastructure.security.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicRepository clinicRepository;

    @GetMapping
    public ResponseEntity<List<ClinicResponseDTO>> listActive() {
        List<ClinicResponseDTO> list =
                clinicRepository.findAllByActiveTrueOrderByBusinessNameAsc().stream()
                        .map(ClinicResponseDTO::fromEntity)
                        .toList();

        return ResponseEntity.ok(list);
    }

    @GetMapping("/me")
    public ResponseEntity<ClinicResponseDTO> myClinic(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        var clinic =
                clinicRepository
                        .findByAdminUserId(principal.getUser().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clínica no encontrada"));

        return ResponseEntity.ok(ClinicResponseDTO.fromEntity(clinic));
    }
}
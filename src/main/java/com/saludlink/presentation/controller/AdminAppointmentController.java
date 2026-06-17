package com.saludlink.presentation.controller;

import com.saludlink.application.dto.AppointmentResponseDTO;
import com.saludlink.domain.model.entity.Appointment;
import com.saludlink.infrastructure.persistence.repository.AppointmentRepository;
import com.saludlink.infrastructure.persistence.repository.ClinicRepository;
import com.saludlink.infrastructure.security.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/appointments")
@RequiredArgsConstructor
public class AdminAppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final ClinicRepository clinicRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<AppointmentResponseDTO>> listMyClinicAppointments(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        var clinic =
                clinicRepository
                        .findByAdminUserId(principal.getUser().getId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Clínica no encontrada"
                        ));

        List<AppointmentResponseDTO> list =
                appointmentRepository.findByClinicId(clinic.getId()).stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(list);
    }

    private AppointmentResponseDTO toResponse(Appointment a) {
        var doctor = a.getDoctor();
        var doctorUser = doctor.getUser();

        var patient = a.getPatient();
        var patientUser = patient.getUser();

        String doctorName = doctorUser.getFirstName() + " " + doctorUser.getLastName();
        String patientName = patientUser.getFirstName() + " " + patientUser.getLastName();

        return AppointmentResponseDTO.builder()
                .id(a.getId())

                .patientId(patient.getId())
                .patientName(patientName)

                .doctorId(doctor.getId())
                .doctorName(doctorName)
                .specialty(doctor.getSpecialty())

                .clinicId(doctor.getClinic() != null ? doctor.getClinic().getId() : null)
                .clinicName(doctor.getClinic() != null ? doctor.getClinic().getBusinessName() : null)

                .branchId(doctor.getBranch() != null ? doctor.getBranch().getId() : null)
                .branchName(doctor.getBranch() != null ? doctor.getBranch().getName() : null)
                .branchAddress(doctor.getBranch() != null ? doctor.getBranch().getAddress() : null)

                .appointmentDate(a.getAppointmentDate())
                .modality(a.getModality())
                .status(a.getStatus())
                .notes(a.getNotes())
                .build();
    }
}
package com.saludlink.application.service.impl;

import com.saludlink.application.dto.AdminDoctorCreateDTO;
import com.saludlink.application.dto.DoctorResponseDTO;
import com.saludlink.application.service.AdminDoctorService;
import com.saludlink.domain.model.entity.Clinic;
import com.saludlink.domain.model.entity.ClinicBranch;
import com.saludlink.domain.model.entity.Doctor;
import com.saludlink.domain.model.entity.User;
import com.saludlink.domain.model.enums.UserRole;
import com.saludlink.infrastructure.persistence.repository.ClinicBranchRepository;
import com.saludlink.infrastructure.persistence.repository.ClinicRepository;
import com.saludlink.infrastructure.persistence.repository.DoctorRepository;
import com.saludlink.infrastructure.persistence.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminDoctorServiceImpl implements AdminDoctorService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final ClinicBranchRepository clinicBranchRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public DoctorResponseDTO registerDoctor(Long adminUserId, AdminDoctorCreateDTO dto) {
        Clinic clinic =
                clinicRepository
                        .findByAdminUserId(adminUserId)
                        .orElseThrow(() -> new IllegalArgumentException("No se encontró la clínica del administrador"));

        ClinicBranch branch =
                clinicBranchRepository
                        .findByIdAndClinicId(dto.getBranchId(), clinic.getId())
                        .orElseThrow(() -> new IllegalArgumentException("La sede seleccionada no pertenece a tu clínica"));

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }

        if (doctorRepository.existsByLicenseNumber(dto.getLicenseNumber())) {
            throw new IllegalArgumentException("La matrícula ya está registrada");
        }

        User user =
                User.builder()
                        .firstName(dto.getFirstName().trim())
                        .lastName(dto.getLastName().trim())
                        .email(dto.getEmail().trim())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .phone(dto.getPhone())
                        .role(UserRole.DOCTOR)
                        .enabled(true)
                        .build();

        User savedUser = userRepository.save(user);

        Doctor doctor =
                Doctor.builder()
                        .user(savedUser)
                        .clinic(clinic)
                        .branch(branch)
                        .specialty(dto.getSpecialty().trim())
                        .licenseNumber(dto.getLicenseNumber().trim())
                        .verified(true)
                        .biography(dto.getBiography())
                        .consultationFee(dto.getConsultationFee())
                        .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        return DoctorResponseDTO.fromEntity(savedDoctor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponseDTO> listDoctorsByAdmin(Long adminUserId) {
        Clinic clinic =
                clinicRepository
                        .findByAdminUserId(adminUserId)
                        .orElseThrow(() -> new IllegalArgumentException("No se encontró la clínica del administrador"));

        return doctorRepository.findByClinicId(clinic.getId()).stream()
                .map(DoctorResponseDTO::fromEntity)
                .toList();
    }
}
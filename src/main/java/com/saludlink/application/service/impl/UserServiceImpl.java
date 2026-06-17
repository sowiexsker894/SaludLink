package com.saludlink.application.service.impl;

import com.saludlink.application.dto.RegisterRequestDTO;
import com.saludlink.application.service.UserService;
import com.saludlink.domain.model.entity.Clinic;
import com.saludlink.domain.model.entity.ClinicBranch;
import com.saludlink.domain.model.entity.Doctor;
import com.saludlink.domain.model.entity.Patient;
import com.saludlink.domain.model.entity.User;
import com.saludlink.domain.model.enums.UserRole;
import com.saludlink.infrastructure.persistence.repository.ClinicBranchRepository;
import com.saludlink.infrastructure.persistence.repository.ClinicRepository;
import com.saludlink.infrastructure.persistence.repository.DoctorRepository;
import com.saludlink.infrastructure.persistence.repository.PatientRepository;
import com.saludlink.infrastructure.persistence.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final ClinicRepository clinicRepository;
    private final ClinicBranchRepository clinicBranchRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerUser(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }

        if (dto.getRole() == UserRole.ADMIN) {
            validateClinicRegistration(dto);
        }

        if (dto.getRole() == UserRole.DOCTOR) {
            validateDoctorRegistration(dto);
        }

        User user =
                User.builder()
                        .firstName(dto.getFirstName())
                        .lastName(dto.getLastName())
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .phone(dto.getPhone())
                        .role(dto.getRole())
                        .build();

        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == UserRole.PATIENT) {
            Patient patient = new Patient();
            patient.setUser(savedUser);
            patientRepository.save(patient);
        }

        if (savedUser.getRole() == UserRole.ADMIN) {
            Clinic clinic =
                    Clinic.builder()
                            .adminUser(savedUser)
                            .businessName(dto.getBusinessName().trim())
                            .establishmentType(dto.getEstablishmentType())
                            .ruc(dto.getRuc().trim())
                            .address(dto.getAddress().trim())
                            .phone(dto.getPhone())
                            .branchesSummary(dto.getBranchesSummary())
                            .active(true)
                            .build();

            clinicRepository.save(clinic);
        }

        if (savedUser.getRole() == UserRole.DOCTOR) {
            Clinic clinic =
                    clinicRepository
                            .findById(dto.getClinicId())
                            .orElseThrow(() -> new IllegalArgumentException("La clínica seleccionada no existe"));

            ClinicBranch branch =
                    clinicBranchRepository
                            .findByIdAndClinicId(dto.getBranchId(), clinic.getId())
                            .orElseThrow(() -> new IllegalArgumentException("La sede seleccionada no pertenece a la clínica"));

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

            doctorRepository.save(doctor);
        }

        return savedUser;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private void validateClinicRegistration(RegisterRequestDTO dto) {
        if (isBlank(dto.getBusinessName())) {
            throw new IllegalArgumentException("La razón social es obligatoria");
        }

        if (isBlank(dto.getRuc())) {
            throw new IllegalArgumentException("El RUC es obligatorio");
        }

        if (dto.getRuc().trim().length() != 11) {
            throw new IllegalArgumentException("El RUC debe tener 11 dígitos");
        }

        if (clinicRepository.existsByRuc(dto.getRuc().trim())) {
            throw new IllegalArgumentException("El RUC ya está registrado");
        }

        if (isBlank(dto.getAddress())) {
            throw new IllegalArgumentException("La dirección de la clínica es obligatoria");
        }
    }

    private void validateDoctorRegistration(RegisterRequestDTO dto) {
        if (dto.getClinicId() == null) {
            throw new IllegalArgumentException("Debe seleccionar una clínica donde labora");
        }

        if (dto.getBranchId() == null) {
            throw new IllegalArgumentException("Debe seleccionar una sede donde labora");
        }

        if (isBlank(dto.getSpecialty())) {
            throw new IllegalArgumentException("La especialidad es obligatoria");
        }

        if (isBlank(dto.getLicenseNumber())) {
            throw new IllegalArgumentException("El número de colegiatura es obligatorio");
        }

        if (doctorRepository.existsByLicenseNumber(dto.getLicenseNumber().trim())) {
            throw new IllegalArgumentException("La matrícula ya está registrada");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
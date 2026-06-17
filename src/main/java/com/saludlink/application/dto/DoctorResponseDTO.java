package com.saludlink.application.dto;

import com.saludlink.domain.model.entity.Doctor;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponseDTO {

    private Long id;

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;

    private String specialty;
    private String licenseNumber;
    private boolean verified;
    private String biography;
    private BigDecimal consultationFee;

    private Long clinicId;
    private String clinicName;

    private Long branchId;
    private String branchName;
    private String branchAddress;

    public static DoctorResponseDTO fromEntity(Doctor doctor) {
        return DoctorResponseDTO.builder()
                .id(doctor.getId())
                .userId(doctor.getUser().getId())
                .firstName(doctor.getUser().getFirstName())
                .lastName(doctor.getUser().getLastName())
                .email(doctor.getUser().getEmail())
                .specialty(doctor.getSpecialty())
                .licenseNumber(doctor.getLicenseNumber())
                .verified(doctor.isVerified())
                .biography(doctor.getBiography())
                .consultationFee(doctor.getConsultationFee())
                .clinicId(doctor.getClinic() != null ? doctor.getClinic().getId() : null)
                .clinicName(doctor.getClinic() != null ? doctor.getClinic().getBusinessName() : null)
                .branchId(doctor.getBranch() != null ? doctor.getBranch().getId() : null)
                .branchName(doctor.getBranch() != null ? doctor.getBranch().getName() : null)
                .branchAddress(doctor.getBranch() != null ? doctor.getBranch().getAddress() : null)
                .build();
    }
}
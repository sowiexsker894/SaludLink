package com.saludlink.application.dto;

import com.saludlink.domain.model.entity.Clinic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicResponseDTO {

    private Long id;
    private String businessName;
    private String establishmentType;
    private String ruc;
    private String address;
    private String phone;
    private String branchesSummary;
    private boolean active;

    public static ClinicResponseDTO fromEntity(Clinic clinic) {
        return ClinicResponseDTO.builder()
                .id(clinic.getId())
                .businessName(clinic.getBusinessName())
                .establishmentType(clinic.getEstablishmentType())
                .ruc(clinic.getRuc())
                .address(clinic.getAddress())
                .phone(clinic.getPhone())
                .branchesSummary(clinic.getBranchesSummary())
                .active(clinic.isActive())
                .build();
    }
}
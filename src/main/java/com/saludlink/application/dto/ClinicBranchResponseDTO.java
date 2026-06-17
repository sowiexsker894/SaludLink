package com.saludlink.application.dto;

import com.saludlink.domain.model.entity.ClinicBranch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicBranchResponseDTO {

    private Long id;
    private Long clinicId;
    private String clinicName;
    private String name;
    private String address;
    private String ruc;
    private boolean active;

    public static ClinicBranchResponseDTO fromEntity(ClinicBranch branch) {
        return ClinicBranchResponseDTO.builder()
                .id(branch.getId())
                .clinicId(branch.getClinic().getId())
                .clinicName(branch.getClinic().getBusinessName())
                .name(branch.getName())
                .address(branch.getAddress())
                .ruc(branch.getRuc())
                .active(branch.isActive())
                .build();
    }
}
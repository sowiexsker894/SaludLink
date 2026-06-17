package com.saludlink.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicBranchRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    private String ruc;
}
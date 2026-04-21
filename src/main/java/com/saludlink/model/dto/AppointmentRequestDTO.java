package com.saludlink.model.dto;

import com.saludlink.model.enums.AppointmentModality;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequestDTO {

    @NotNull private Long doctorId;

    @NotNull private LocalDateTime appointmentDate;

    @NotNull private AppointmentModality modality;

    private String notes;
}

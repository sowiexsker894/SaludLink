package com.saludlink.service;

import com.saludlink.model.dto.AppointmentRequestDTO;
import com.saludlink.model.dto.AppointmentResponseDTO;
import com.saludlink.model.enums.AppointmentStatus;
import java.util.List;

public interface AppointmentService {

    AppointmentResponseDTO createAppointment(Long patientId, AppointmentRequestDTO dto);

    List<AppointmentResponseDTO> getAppointmentsByPatient(Long patientId);

    List<AppointmentResponseDTO> getAppointmentsByDoctor(Long doctorId);

    void cancelAppointment(Long appointmentId);

    void updateAppointmentStatus(Long id, AppointmentStatus status);
}

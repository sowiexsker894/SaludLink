package com.saludlink.application.service;

import com.saludlink.application.dto.AdminDoctorCreateDTO;
import com.saludlink.application.dto.DoctorResponseDTO;
import java.util.List;

public interface AdminDoctorService {

    DoctorResponseDTO registerDoctor(Long adminUserId, AdminDoctorCreateDTO dto);

    List<DoctorResponseDTO> listDoctorsByAdmin(Long adminUserId);
}
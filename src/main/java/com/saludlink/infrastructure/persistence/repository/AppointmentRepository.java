package com.saludlink.infrastructure.persistence.repository;

import com.saludlink.domain.model.entity.Appointment;
import com.saludlink.domain.model.enums.AppointmentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
            SELECT a FROM Appointment a
            JOIN FETCH a.patient p
            JOIN FETCH p.user
            JOIN FETCH a.doctor d
            JOIN FETCH d.user
            LEFT JOIN FETCH d.clinic
            LEFT JOIN FETCH d.branch
            WHERE a.patient.id = :patientId
            ORDER BY a.appointmentDate DESC
            """)
    List<Appointment> findByPatientId(@Param("patientId") Long patientId);

    @Query("""
            SELECT a FROM Appointment a
            JOIN FETCH a.patient p
            JOIN FETCH p.user
            JOIN FETCH a.doctor d
            JOIN FETCH d.user
            LEFT JOIN FETCH d.clinic
            LEFT JOIN FETCH d.branch
            WHERE a.doctor.id = :doctorId
            ORDER BY a.appointmentDate DESC
            """)
    List<Appointment> findByDoctorId(@Param("doctorId") Long doctorId);

    @Query("""
            SELECT a FROM Appointment a
            JOIN FETCH a.patient p
            JOIN FETCH p.user
            JOIN FETCH a.doctor d
            JOIN FETCH d.user
            LEFT JOIN FETCH d.clinic
            LEFT JOIN FETCH d.branch
            WHERE d.clinic.id = :clinicId
            ORDER BY a.appointmentDate DESC
            """)
    List<Appointment> findByClinicId(@Param("clinicId") Long clinicId);

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.doctor.id = :doctorId
            AND a.status = :status
            """)
    List<Appointment> findByDoctorIdAndStatus(
            @Param("doctorId") Long doctorId,
            @Param("status") AppointmentStatus status
    );

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.patient.id = :patientId
            AND a.status = :status
            """)
    List<Appointment> findByPatientIdAndStatus(
            @Param("patientId") Long patientId,
            @Param("status") AppointmentStatus status
    );
}
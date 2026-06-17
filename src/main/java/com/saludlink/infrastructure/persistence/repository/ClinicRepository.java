package com.saludlink.infrastructure.persistence.repository;

import com.saludlink.domain.model.entity.Clinic;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {

    boolean existsByRuc(String ruc);

    Optional<Clinic> findByAdminUserId(Long adminUserId);

    List<Clinic> findAllByActiveTrueOrderByBusinessNameAsc();
}
package com.saludlink.infrastructure.persistence.repository;

import com.saludlink.domain.model.entity.ClinicBranch;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicBranchRepository extends JpaRepository<ClinicBranch, Long> {

    List<ClinicBranch> findByClinicIdAndActiveTrueOrderByNameAsc(Long clinicId);

    Optional<ClinicBranch> findByIdAndClinicId(Long id, Long clinicId);
}
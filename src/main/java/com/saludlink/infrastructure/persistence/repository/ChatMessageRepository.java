package com.saludlink.infrastructure.persistence.repository;

import com.saludlink.domain.model.entity.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByAppointmentIdOrderBySentAtAsc(Long appointmentId);
}

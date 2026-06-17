package com.saludlink.presentation.controller;

import com.saludlink.application.dto.ChatMessageRequestDTO;
import com.saludlink.application.dto.ChatMessageResponseDTO;
import com.saludlink.domain.model.entity.Appointment;
import com.saludlink.domain.model.entity.ChatMessage;
import com.saludlink.domain.model.entity.User;
import com.saludlink.infrastructure.persistence.repository.AppointmentRepository;
import com.saludlink.infrastructure.persistence.repository.ChatMessageRepository;
import com.saludlink.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final AppointmentRepository appointmentRepository;

    @GetMapping("/appointments/{appointmentId}/messages")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ChatMessageResponseDTO>> listMessages(
            @PathVariable Long appointmentId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Appointment appointment = getAppointmentOrThrow(appointmentId);
        validateAccess(appointment, principal.getUser());

        List<ChatMessageResponseDTO> messages =
                chatMessageRepository.findByAppointmentIdOrderBySentAtAsc(appointmentId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(messages);
    }

    @PostMapping("/appointments/{appointmentId}/messages")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR','ADMIN')")
    @Transactional
    public ResponseEntity<ChatMessageResponseDTO> sendMessage(
            @PathVariable Long appointmentId,
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ChatMessageRequestDTO dto
    ) {
        Appointment appointment = getAppointmentOrThrow(appointmentId);
        validateAccess(appointment, principal.getUser());

        ChatMessage saved =
                chatMessageRepository.save(
                        ChatMessage.builder()
                                .appointment(appointment)
                                .sender(principal.getUser())
                                .message(dto.getMessage().trim())
                                .build()
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    private Appointment getAppointmentOrThrow(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cita no encontrada"
                ));
    }

    private void validateAccess(Appointment appointment, User user) {
        String role = user.getRole().name();

        if ("ADMIN".equals(role)) {
            return;
        }

        if ("PATIENT".equals(role)) {
            boolean isOwnerPatient =
                    appointment.getPatient() != null
                            && appointment.getPatient().getUser() != null
                            && appointment.getPatient().getUser().getId().equals(user.getId());

            if (isOwnerPatient) {
                return;
            }
        }

        if ("DOCTOR".equals(role)) {
            boolean isOwnerDoctor =
                    appointment.getDoctor() != null
                            && appointment.getDoctor().getUser() != null
                            && appointment.getDoctor().getUser().getId().equals(user.getId());

            if (isOwnerDoctor) {
                return;
            }
        }

        throw new AccessDeniedException("No tienes acceso al chat de esta cita");
    }

    private ChatMessageResponseDTO toResponse(ChatMessage message) {
        User sender = message.getSender();

        String senderName =
                (sender.getFirstName() + " " + sender.getLastName()).trim();

        return ChatMessageResponseDTO.builder()
                .id(message.getId())
                .appointmentId(message.getAppointment().getId())
                .senderUserId(sender.getId())
                .senderName(senderName)
                .senderRole(sender.getRole().name())
                .message(message.getMessage())
                .sentAt(message.getSentAt())
                .build();
    }
}

package com.saludlink.application.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageResponseDTO {

    private Long id;

    private Long appointmentId;

    private Long senderUserId;

    private String senderName;

    private String senderRole;

    private String message;

    private LocalDateTime sentAt;
}

package com.saludlink.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatMessageRequestDTO {

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 2000, message = "El mensaje no puede superar 2000 caracteres")
    private String message;
}

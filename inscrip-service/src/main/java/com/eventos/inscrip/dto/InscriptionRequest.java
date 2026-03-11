package com.eventos.inscrip.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InscriptionRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Event ID is required")
    private Long eventId;

    private String status = "CONFIRMED";
}

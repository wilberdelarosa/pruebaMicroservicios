package com.eventos.inscrip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InscriptionResponse {
    private Long id;
    private Long userId;
    private Long eventId;
    private LocalDateTime inscriptionDate;
    private String status;
    private EventDTO event;
}

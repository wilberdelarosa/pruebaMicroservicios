package com.eventos.inscrip.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventDTO {
    private Long id;
    private String name;
    private String location;
    private LocalDateTime eventDate;
    private Integer availableSpots;
    private String category;
    private Double price;
    private String imageUrl;
}

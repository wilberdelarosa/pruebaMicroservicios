package com.eventos.inscrip.controller;

import com.eventos.inscrip.dto.EventDTO;
import com.eventos.inscrip.dto.InscriptionRequest;
import com.eventos.inscrip.dto.InscriptionResponse;
import com.eventos.inscrip.model.Inscription;
import com.eventos.inscrip.repository.InscriptionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionRepository inscriptionRepository;
    private final RestTemplate restTemplate;

    @Value("${event-service.url}")
    private String eventServiceUrl;

    @PostMapping
    public ResponseEntity<?> createInscription(@Valid @RequestBody InscriptionRequest request, HttpServletRequest httpRequest) {
        
        // Optional security check (filter already parsed token if valid)
        Object tokenUserId = httpRequest.getAttribute("userId");
        if(tokenUserId != null && !tokenUserId.toString().equals(request.getUserId().toString())) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Token user does not match request body user"));
        }

        if (inscriptionRepository.existsByUserIdAndEventId(request.getUserId(), request.getEventId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "User is already inscribed to this event"));
        }

        // 1. Fetch event from EventService
        EventDTO event;
        try {
            ResponseEntity<EventDTO> eventResponse = restTemplate.getForEntity(
                    eventServiceUrl + "/events/" + request.getEventId(),
                    EventDTO.class
            );
            event = eventResponse.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Event not found"));
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error communicating with Event Service"));
        }

        // 2. Validate availability
        if (event == null || event.getAvailableSpots() <= 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "No available spots for this event"));
        }

        // 3. Decrement spots in EventService
        try {
            restTemplate.put(eventServiceUrl + "/events/" + request.getEventId() + "/decrement-spots", null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to decrement spots in Event Service"));
        }

        // 4. Save inscription locally
        Inscription inscription = Inscription.builder()
                .userId(request.getUserId())
                .eventId(request.getEventId())
                .status(request.getStatus())
                .build();
        
        inscription = inscriptionRepository.save(inscription);

        // 5. Build Response
        InscriptionResponse response = InscriptionResponse.builder()
                .id(inscription.getId())
                .userId(inscription.getUserId())
                .eventId(inscription.getEventId())
                .inscriptionDate(inscription.getInscriptionDate())
                .status(inscription.getStatus())
                .event(event)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InscriptionResponse>> getUserInscriptions(@PathVariable Long userId, HttpServletRequest httpRequest) {
        // Optional security check
        Object tokenUserId = httpRequest.getAttribute("userId");
        if(tokenUserId != null && !tokenUserId.toString().equals(userId.toString())) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Inscription> inscriptions = inscriptionRepository.findByUserId(userId);

        List<InscriptionResponse> responseList = inscriptions.stream().map(inscrip -> {
            EventDTO event = null;
            try {
                ResponseEntity<EventDTO> eventResponse = restTemplate.getForEntity(
                        eventServiceUrl + "/events/" + inscrip.getEventId(),
                        EventDTO.class
                );
                event = eventResponse.getBody();
            } catch (Exception ignored) {
                // Return null event if service is down, but still return the inscription
            }

            return InscriptionResponse.builder()
                    .id(inscrip.getId())
                    .userId(inscrip.getUserId())
                    .eventId(inscrip.getEventId())
                    .inscriptionDate(inscrip.getInscriptionDate())
                    .status(inscrip.getStatus())
                    .event(event)
                    .build();
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
}

package com.eventos.event.controller;

import com.eventos.event.model.Event;
import com.eventos.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;

    @GetMapping
    public ResponseEntity<List<Event>> getAllActiveEvents() {
        return ResponseEntity.ok(eventRepository.findByActiveTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // This is currently an internal endpoint for the Inscription Service to call
    @PutMapping("/{id}/decrement-spots")
    public ResponseEntity<?> decrementSpots(@PathVariable Long id) {
        return eventRepository.findById(id).map(event -> {
            if (event.getAvailableSpots() > 0) {
                event.setAvailableSpots(event.getAvailableSpots() - 1);
                eventRepository.save(event);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "availableSpotsAfter", event.getAvailableSpots(),
                        "message", "Spot decremented successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "No available spots"));
            }
        }).orElse(ResponseEntity.notFound().build());
    }
}

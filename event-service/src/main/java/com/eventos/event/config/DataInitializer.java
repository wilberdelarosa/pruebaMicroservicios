package com.eventos.event.config;

import com.eventos.event.model.Event;
import com.eventos.event.repository.EventRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final EventRepository eventRepository;

    @PostConstruct
    public void init() {
        if (eventRepository.count() == 0) {
            List<Event> eventos = List.of(
                    Event.builder()
                            .name("Festival de Música Caribeña")
                            .description("Vive la mejor música del Caribe con artistas locales y gastronomía en vivo. Un ambiente imperdible.")
                            .location("Playa Bávaro, Punta Cana")
                            .eventDate(LocalDateTime.now().plusDays(20).withHour(18).withMinute(0))
                            .totalCapacity(500)
                            .availableSpots(500)
                            .category("Música")
                            .price(45.99)
                            .imageUrl("https://images.unsplash.com/photo-1459749411175-04bf5292ceea?q=80&w=2670&auto=format&fit=crop")
                            .build(),
                    Event.builder()
                            .name("Tournedos Steak Night")
                            .description("Cena exclusiva de carnes a la parrilla bajo las estrellas, maridaje de vinos incluido.")
                            .location("Restaurante Boulevard, Cap Cana")
                            .eventDate(LocalDateTime.now().plusDays(35).withHour(20).withMinute(0))
                            .totalCapacity(50)
                            .availableSpots(50)
                            .category("Gastronomía")
                            .price(89.99)
                            .imageUrl("https://images.unsplash.com/photo-1555939594-58d7cb561ad1?q=80&w=2574&auto=format&fit=crop")
                            .build(),
                    Event.builder()
                            .name("Expedición a Cueva Fun Fun")
                            .description("Cabalgata, caminata por la selva y descenso en la majestuosa cueva Fun Fun.")
                            .location("Rancho Capote, Hato Mayor")
                            .eventDate(LocalDateTime.now().plusDays(15).withHour(7).withMinute(30))
                            .totalCapacity(30)
                            .availableSpots(30)
                            .category("Aventura")
                            .price(120.00)
                            .imageUrl("https://images.unsplash.com/photo-1544644181-1484b3fdfc62?q=80&w=2670&auto=format&fit=crop")
                            .build()
            );

            eventRepository.saveAll(eventos);
        }
    }
}

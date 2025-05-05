package com.gustavo.events_microservice.controllers;

import com.gustavo.events_microservice.domain.Event;
import com.gustavo.events_microservice.dtos.EventRequestDTO;
import com.gustavo.events_microservice.dtos.SubscriptionRequestDTO;
import com.gustavo.events_microservice.dtos.SubscriptionResponseDTO;
import com.gustavo.events_microservice.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/upcoming")
    public List<Event> getUpcomingEvents() {
        return eventService.getUpcomingEvents();
    }

    @PostMapping
    public Event createEvent(@RequestBody @Valid EventRequestDTO event) {
        return eventService.createEvent(event);
    }

    // no @PathVariable vamos passar o ID do evento, enquanto no corpo da requisição (@RequestBody) vamos passar apenas o email do participante
    // PathVariable = url, RequestBody = json
    @PostMapping("/{eventId}/register")
    public SubscriptionResponseDTO registerParticipant(@PathVariable String eventId, @RequestBody @Valid SubscriptionRequestDTO subscriptionRequest) {
        return eventService.registerParticipant(eventId, subscriptionRequest.participantEmail(), subscriptionRequest.name());
    }
}

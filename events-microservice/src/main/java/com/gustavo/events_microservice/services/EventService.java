package com.gustavo.events_microservice.services;

import com.gustavo.events_microservice.domain.Event;
import com.gustavo.events_microservice.domain.Subscription;
import com.gustavo.events_microservice.dtos.EventRequestDTO;
import com.gustavo.events_microservice.exceptions.EventFullException;
import com.gustavo.events_microservice.exceptions.EventNotFoundException;
import com.gustavo.events_microservice.repositories.EventRepository;
import com.gustavo.events_microservice.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final SubscriptionRepository subscriptionRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now());
    }

    public Event createEvent(EventRequestDTO eventRequest) {
        var event = new Event(eventRequest);
        return eventRepository.save(event);
    }

    private Boolean isEventFull(Event event) {
        // se numero de participantes for maior ou igual que o máximo permitido de participantes no evento ele retorna o EvenFullException
        return event.getRegisteredParticipants() >= event.getMaxParticipants();
    }

    public void registerParticipant(String eventId, String participantEmail) {
        var event = eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        // se numero de participantes for maior ou igual que o máximo permitido de participantes no evento ele retorna o EvenFullException
        if (isEventFull(event)) {
            throw new EventFullException();
        }

        // mas caso n retorne o EventFullException, ele registra o parcitipante no evento
        var subscription = new Subscription(event, participantEmail);
        subscriptionRepository.save(subscription);

        event.setRegisteredParticipants(event.getRegisteredParticipants() + 1);
    }
}

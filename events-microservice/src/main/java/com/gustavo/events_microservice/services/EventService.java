package com.gustavo.events_microservice.services;

import com.gustavo.events_microservice.domain.Event;
import com.gustavo.events_microservice.domain.User;
import com.gustavo.events_microservice.dtos.EventRequestDTO;
import com.gustavo.events_microservice.dtos.SubscriptionResponseDTO;
import com.gustavo.events_microservice.exceptions.EventFullException;
import com.gustavo.events_microservice.exceptions.EventNotFoundException;
import com.gustavo.events_microservice.producers.EventProducer;
import com.gustavo.events_microservice.repositories.EventRepository;
import com.gustavo.events_microservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventProducer eventProducer;

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

    public SubscriptionResponseDTO registerParticipant(String eventId, String participantEmail, String name) {
        var event = eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        // se numero de participantes for maior ou igual que o máximo permitido de participantes no evento ele retorna o EvenFullException
        if (isEventFull(event)) {
            throw new EventFullException();
        }

        // mas caso n retorne o EventFullException, ele registra o parcitipante no evento
        var subscription = new User(event, participantEmail, name);
        userRepository.save(subscription);

        // atualiza o numero de participantes
        event.setRegisteredParticipants(event.getRegisteredParticipants() + 1);

        // atualiza o email via rabbitmq
        eventProducer.publishMessageEmail(subscription, event);

        return new SubscriptionResponseDTO(
                "Inscrição realizada com sucesso!",
                event.getTitle(),
                event.getDate().toString()
        );
    }
}

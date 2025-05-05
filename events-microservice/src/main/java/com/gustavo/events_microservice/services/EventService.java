package com.gustavo.events_microservice.services;

import com.gustavo.events_microservice.domain.Event;
import com.gustavo.events_microservice.domain.User;
import com.gustavo.events_microservice.dtos.EventRequestDTO;
import com.gustavo.events_microservice.dtos.SubscriptionResponseDTO;
import com.gustavo.events_microservice.producers.EventProducer;
import com.gustavo.events_microservice.repositories.EventRepository;
import com.gustavo.events_microservice.repositories.UserRepository;
import com.gustavo.events_microservice.services.exceptions.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventProducer eventProducer;

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Event> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now());
    }

    public Event createEvent(EventRequestDTO eventRequest) {
        if (eventRequest.date().isBefore(LocalDate.now())) {
            throw new InvalidEventDateException("A data do evento deve ser no futuro.");
        }
        var event = new Event(eventRequest);
        return eventRepository.save(event);
    }

    @Transactional
    public EventRequestDTO updateEvent(String id, EventRequestDTO dto) {
        try {
            Event entity = eventRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);

            if (entity.getDate().isBefore(LocalDate.now())) {
                throw new InvalidEventDateException("A data do evento deve ser no futuro.");
            }

            entity = eventRepository.save(entity);
            return new EventRequestDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Evento não encontrado.");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteEvent(String id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evento não existe.");
        } try {
            eventRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial.");
        }
        eventRepository.deleteById(id);
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

        // verifica se o usuario ja esta inscrito no evento
        boolean alreadySubscribed = userRepository.existsByEventAndParticipantEmail(event, participantEmail);
        if (alreadySubscribed) {
            throw new DuplicateSubscriptionException("Usuário já está cadastrado no evento.");
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

    private void copyDtoToEntity(EventRequestDTO dto, Event entity) {
        entity.setDate(dto.date());
        entity.setDescription(dto.description());
        entity.setTitle(dto.title());
        entity.setRegisteredParticipants(dto.registeredParticipants());
        entity.setMaxParticipants(dto.maxParticipants());
    }
}

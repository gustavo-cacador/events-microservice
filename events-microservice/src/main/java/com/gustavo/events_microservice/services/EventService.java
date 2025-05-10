package com.gustavo.events_microservice.services;

import com.gustavo.events_microservice.domain.Category;
import com.gustavo.events_microservice.domain.Event;
import com.gustavo.events_microservice.domain.User;
import com.gustavo.events_microservice.dtos.CategoryDTO;
import com.gustavo.events_microservice.dtos.EventRequestDTO;
import com.gustavo.events_microservice.dtos.SubscriptionResponseDTO;
import com.gustavo.events_microservice.producers.EventProducer;
import com.gustavo.events_microservice.repositories.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Event> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<EventRequestDTO> searchByName(String title) {
        List<Event> list = eventRepository.searchByName(title);
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum evento encontrado com o título: " + title);
        }
        return list.stream().map(x -> new EventRequestDTO(x)).toList();
    }

    public EventRequestDTO createEvent(EventRequestDTO eventRequest) {
        if (eventRequest.getDate().isBefore(LocalDate.now())) {
            throw new InvalidEventDateException("A data do evento deve ser no futuro.");
        }
        Event event = new Event();
        copyDtoToEntity(eventRequest, event);
        event = eventRepository.save(event);
        return new EventRequestDTO(event);
    }

    @Transactional
    public EventRequestDTO updateEvent(Long id, EventRequestDTO dto) {
        try {
            Event entity = eventRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);

            // verifica se a nova data do evento n está no passado
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
    public void deleteEvent(Long id) {
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

    @Transactional
    public SubscriptionResponseDTO registerParticipant(Long eventId, String participantEmail, String name, String telefone) {
        var event = eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);

        // se numero de participantes for maior ou igual que o máximo permitido de participantes no evento ele retorna o EvenFullException
        if (isEventFull(event)) {
            throw new EventFullException("O evento já atingiu o número máximo de participantes.");
        }

        // verifica se o usuario ja esta inscrito no evento
        boolean alreadySubscribed = userRepository.existsByEventAndParticipantEmail(event, participantEmail);
        if (alreadySubscribed) {
            throw new DuplicateSubscriptionException("Usuário já está cadastrado no evento.");
        }

        // mas caso n retorne o EventFullException, ele registra o parcitipante no evento
        var subscription = new User(event, participantEmail, name, telefone);
        userRepository.save(subscription);

        // atualiza o numero de participantes
        event.setRegisteredParticipants(event.getRegisteredParticipants() + 1);

        eventRepository.save(event);

        // atualiza o email via rabbitmq
        eventProducer.publishMessageEmail(subscription, event);

        return new SubscriptionResponseDTO(
                "Inscrição realizada com sucesso!",
                event.getTitle(),
                event.getDate().toString()
        );
    }

    private void copyDtoToEntity(EventRequestDTO dto, Event entity) {
        entity.setDate(dto.getDate());
        entity.setDescription(dto.getDescription());
        entity.setTitle(dto.getTitle());
        entity.setRegisteredParticipants(dto.getRegisteredParticipants());
        entity.setMaxParticipants(dto.getMaxParticipants());

        entity.getCategories().clear();
        for (CategoryDTO categoryDTO : dto.getCategories()) {
            var category = categoryRepository.findById(categoryDTO.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Categoria com id: " + categoryDTO.getId() + ", não encontrado."));
            entity.getCategories().add(category);
        }
    }
}

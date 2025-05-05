package com.gustavo.events_microservice.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gustavo.events_microservice.domain.Event;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EventRequestDTO(
        @NotNull
        @Min(1)
        int maxParticipants,

        @Min(0)
        int registeredParticipants,

        @NotNull
        @Future(message = "A data do evento deve ser no futuro.")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate date,

        @NotBlank
        @Size(min = 3, max = 120)
        String title,

        @Size(max = 450)
        String description
) {
        public EventRequestDTO(Event entity) {
                this(
                        entity.getMaxParticipants(),
                        entity.getRegisteredParticipants(),
                        entity.getDate(),
                        entity.getTitle(),
                        entity.getDescription()
                );
        }
}

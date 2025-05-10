package com.gustavo.events_microservice.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gustavo.events_microservice.domain.Category;
import com.gustavo.events_microservice.domain.Event;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventRequestDTO {

        private Long id;

        @NotNull
        @Min(1)
        int maxParticipants;

        @Min(0)
        int registeredParticipants;

        @NotNull
        @Future(message = "A data do evento deve ser no futuro.")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate date;

        @NotBlank
        @Size(min = 3, max = 120)
        String title;

        @Size(max = 450)
        String description;

        @NotEmpty(message = "Deve ter pelo menos uma categoria.")
        private List<CategoryDTO> categories = new ArrayList<>();

        public EventRequestDTO() {
        }

        public EventRequestDTO(Long id, int maxParticipants, int registeredParticipants, LocalDate date, String title, String description) {
                this.id = id;
                this.maxParticipants = maxParticipants;
                this.registeredParticipants = registeredParticipants;
                this.date = date;
                this.title = title;
                this.description = description;
        }

        public EventRequestDTO(Event entity) {
                id = entity.getId();
                maxParticipants = entity.getMaxParticipants();
                registeredParticipants = entity.getRegisteredParticipants();
                date = entity.getDate();
                title = entity.getTitle();
                description = entity.getDescription();

                for (Category category : entity.getCategories()) {
                        categories.add(new CategoryDTO(category));
                }
        }

        public Long getId() {
                return id;
        }

        public int getMaxParticipants() {
                return maxParticipants;
        }

        public int getRegisteredParticipants() {
                return registeredParticipants;
        }

        public LocalDate getDate() {
                return date;
        }

        public String getTitle() {
                return title;
        }

        public String getDescription() {
                return description;
        }

        public List<CategoryDTO> getCategories() {
                return categories;
        }
}

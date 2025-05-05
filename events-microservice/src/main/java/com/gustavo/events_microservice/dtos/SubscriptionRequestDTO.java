package com.gustavo.events_microservice.dtos;

import jakarta.validation.constraints.NotBlank;

public record SubscriptionRequestDTO(
        @NotBlank String name,
        @NotBlank String participantEmail
) {
}

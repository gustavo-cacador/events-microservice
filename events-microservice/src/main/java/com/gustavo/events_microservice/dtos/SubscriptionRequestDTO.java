package com.gustavo.events_microservice.dtos;

public record SubscriptionRequestDTO(
        String name,
        String participantEmail
) {
}

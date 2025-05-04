package com.gustavo.events_microservice.dtos;

public record SubscriptionResponseDTO(
        String message,
        String eventName,
        String eventDate
) {
}

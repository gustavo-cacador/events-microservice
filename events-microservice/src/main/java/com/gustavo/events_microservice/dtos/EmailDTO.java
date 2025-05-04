package com.gustavo.events_microservice.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class EmailDTO {

    private UUID userId;
    private String emailTo;
    private String subject;
    private String text;
}

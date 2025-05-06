package com.gustavo.events_microservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userId;
    private String name;
    private String telefone;

    @ManyToOne
    private Event event;

    private String participantEmail;

    public User(Event event, String participantEmail, String name, String telefone) {
        this.event = event;
        this.participantEmail = participantEmail;
        this.name = name;
        this.telefone = telefone;
    }
}

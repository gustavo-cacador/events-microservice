package com.gustavo.email_microservice.repositories;

import com.gustavo.email_microservice.domain.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailRepository extends JpaRepository<Email, UUID> {
}

package com.gustavo.events_microservice.repositories;

import com.gustavo.events_microservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

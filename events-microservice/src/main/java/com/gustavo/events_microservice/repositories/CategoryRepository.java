package com.gustavo.events_microservice.repositories;

import com.gustavo.events_microservice.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

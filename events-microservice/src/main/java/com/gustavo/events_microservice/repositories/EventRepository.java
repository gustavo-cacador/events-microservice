package com.gustavo.events_microservice.repositories;

import com.gustavo.events_microservice.domain.Event;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, String> {

    // query para retornar os eventos cuja data seja futura
    // usamos PARSE para converter a coluna data de String para data/hora
    @Query(value = "SELECT * FROM events e WHERE PARSEDATETIME(e.date, 'dd/MM/yyyy') > :currentDate", nativeQuery = true)
    List<Event> findUpcomingEvents(@Param("currentDate") LocalDateTime currentDate);

    @NonNull
    Optional<Event> findById(@NonNull String id);
}

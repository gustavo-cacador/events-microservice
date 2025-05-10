package com.gustavo.events_microservice.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int maxParticipants;
    private int registeredParticipants;
    private LocalDate date;
    private String title;
    private String description;

    @ManyToMany
    @JoinTable(name = "events_categories",
            joinColumns = @JoinColumn(name = "events_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    @JsonManagedReference
    private Set<Category> categories = new HashSet<>();

    public Event() {
    }

    public Event(Long id, int maxParticipants, int registeredParticipants, LocalDate date, String title, String description, Set<Category> categories) {
        this.id = id;
        this.maxParticipants = maxParticipants;
        this.registeredParticipants = registeredParticipants;
        this.date = date;
        this.title = title;
        this.description = description;
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getRegisteredParticipants() {
        return registeredParticipants;
    }

    public void setRegisteredParticipants(int registeredParticipants) {
        this.registeredParticipants = registeredParticipants;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

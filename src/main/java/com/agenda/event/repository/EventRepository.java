package com.agenda.event.repository;

import com.agenda.event.model.Event;
import com.agenda.event.model.EventId;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event save(Event event);

    Optional<Event> findById(EventId eventId);

    List<Event> findAll();

    void deleteById(EventId eventId);
}

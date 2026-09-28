package com.agenda.infrastructure.sql.dao;

import com.agenda.common.persistence.DatabaseConnection;
import com.agenda.event.model.Event;
import com.agenda.event.model.RecurrenceType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventSqlDaoTest {
    private EventSqlDao eventSqlDao;

    @BeforeEach
    void setUp() {
        eventSqlDao = new EventSqlDao();
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM event WHERE text LIKE 'EventSqlDaoTest%'")) {

            statement.executeUpdate();
        }
    }

    @Test
    void shouldSaveNewEvent() {
        Event event = new Event("EventSqlDaoTest Event", LocalDate.of(2026, 10, 10));

        Event savedEvent = eventSqlDao.save(event);

        assertNotNull(savedEvent.getEventId());
        assertEquals("EventSqlDaoTest Event", savedEvent.getText());
        assertEquals(LocalDate.of(2026, 10, 10), savedEvent.getEventDate());
        assertEquals(RecurrenceType.NONE, savedEvent.getRecurrenceType());
        assertNull(savedEvent.getRepeatUntil());
    }

    @Test
    void shouldFindEventById() {
        Event savedEvent = eventSqlDao.save(new Event("EventSqlDaoTest Find event", LocalDate.of(2026, 10,
                15)));

        Event foundEvent = eventSqlDao.findById(savedEvent.getEventId()).orElseThrow();

        assertEquals(savedEvent.getEventId(), foundEvent.getEventId());
        assertEquals("EventSqlDaoTest Find event", foundEvent.getText());
        assertEquals(LocalDate.of(2026, 10, 15), foundEvent.getEventDate());
        assertEquals(RecurrenceType.NONE, foundEvent.getRecurrenceType());
    }

    @Test
    void shouldUpdateEvent() {
        Event savedEvent = eventSqlDao.save(new Event("EventSqlDaoTest Original event", LocalDate.of(2026,
                10, 5)));

        savedEvent.updateDetails("EventSqlDaoTest Updated event", LocalDate.of(2026, 12,
                21));

        savedEvent.configureRecurrence(RecurrenceType.MONTHLY, LocalDate.of(2027, 1, 21));

        eventSqlDao.save(savedEvent);

        Event updatedEvent = eventSqlDao.findById(savedEvent.getEventId()).orElseThrow();

        assertEquals("EventSqlDaoTest Updated event", updatedEvent.getText());
        assertEquals(LocalDate.of(2026, 12, 21), updatedEvent.getEventDate());
        assertEquals(RecurrenceType.MONTHLY, updatedEvent.getRecurrenceType());
        assertEquals(LocalDate.of(2027, 1, 21), updatedEvent.getRepeatUntil());
    }

    @Test
    void shouldDeleteEvent() {
        Event savedEvent = eventSqlDao.save(new Event("EventSqlDaoTest Delete event", LocalDate.of(2026,
                10, 10)));

        eventSqlDao.deleteById(savedEvent.getEventId());

        assertTrue(eventSqlDao.findById(savedEvent.getEventId()).isEmpty());
    }

    @Test
    void shouldFindAllEvents() {
        Event firstEvent = eventSqlDao.save(new Event("EventSqlDaoTest Event 1", LocalDate.of(2026, 10,
                12)));

        Event secondEvent = eventSqlDao.save(new Event("EventSqlDaoTest Event 2", LocalDate.of(2026,
                11, 7)));

        List<Event> events = eventSqlDao.findAll();

        assertTrue(events.stream().anyMatch(event -> event.getEventId().equals(firstEvent.getEventId())));

        assertTrue(events.stream().anyMatch(event -> event.getEventId().equals(secondEvent.getEventId())));
    }
}

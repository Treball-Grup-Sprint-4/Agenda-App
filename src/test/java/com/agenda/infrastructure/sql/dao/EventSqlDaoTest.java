package com.agenda.infrastructure.sql.dao;

import com.agenda.common.exception.PersistenceException;
import com.agenda.common.persistence.DatabaseConnection;
import com.agenda.event.model.Event;
import com.agenda.event.model.EventId;
import com.agenda.event.model.RecurrenceType;
import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
             PreparedStatement taskStatement = connection.prepareStatement(
                     "DELETE FROM task WHERE text LIKE 'EventSqlDaoTest%'");
             PreparedStatement eventStatement = connection.prepareStatement(
                     "DELETE FROM event WHERE text LIKE 'EventSqlDaoTest%'")) {

            taskStatement.executeUpdate();
            eventStatement.executeUpdate();
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
    void shouldPersistAssociatedTaskWhenSavingNewEvent() throws SQLException {
        int taskId;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO task (text, priority, status, created_at)
                 VALUES (?, ?, ?, CURRENT_TIMESTAMP)
                 """, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, "EventSqlDaoTest New event task");
            statement.setString(2, "MEDIUM");
            statement.setString(3, "PENDING");

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                taskId = generatedKeys.getInt(1);
            }
        }

        Event event = new Event("EventSqlDaoTest New event association",
                LocalDate.of(2026, 10, 10));

        event.addTask(new TaskId(taskId));

        Event savedEvent = eventSqlDao.save(event);

        Event foundEvent = eventSqlDao.findById(savedEvent.getEventId()).orElseThrow();

        assertEquals(List.of(new TaskId(taskId)), foundEvent.getTaskIds());
    }

    @Test
    void shouldReconstructAssociatedTaskIdsWhenFindingEventById() throws SQLException {
        Event savedEvent = eventSqlDao.save(new Event("EventSqlDaoTest Associated task",
                LocalDate.of(2026, 10, 10)));

        int taskId;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO task (text, priority, status, created_at, event_id)
                 VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)
                 """, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, "EventSqlDaoTest Associated task");
            statement.setString(2, "MEDIUM");
            statement.setString(3, "PENDING");
            statement.setInt(4, savedEvent.getEventId().value());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                taskId = generatedKeys.getInt(1);
            }
        }

        Event foundEvent = eventSqlDao.findById(savedEvent.getEventId()).orElseThrow();

        assertEquals(List.of(new TaskId(taskId)), foundEvent.getTaskIds());
    }

    @Test
    void shouldPersistAssociatedTaskWhenSavingEvent() throws SQLException {
        Event savedEvent = eventSqlDao.save(new Event("EventSqlDaoTest Persist association",
                LocalDate.of(2026, 10, 10)));

        int taskId;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO task (text, priority, status, created_at)
                 VALUES (?, ?, ?, CURRENT_TIMESTAMP)
                 """, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, "EventSqlDaoTest Persist task");
            statement.setString(2, "MEDIUM");
            statement.setString(3, "PENDING");

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                taskId = generatedKeys.getInt(1);
            }
        }

        savedEvent.addTask(new TaskId(taskId));

        eventSqlDao.save(savedEvent);

        Event foundEvent = eventSqlDao.findById(savedEvent.getEventId()).orElseThrow();

        assertEquals(List.of(new TaskId(taskId)), foundEvent.getTaskIds());
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
    void shouldThrowExceptionWhenUpdatingNonExistingEvent() {
        Event savedEvent = eventSqlDao.save(new Event("EventSqlDaoTest Deleted event",
                LocalDate.of(2026, 12, 8)));

        eventSqlDao.deleteById(savedEvent.getEventId());

        Event event = new Event(
                savedEvent.getEventId(), "EventSqlDaoTest Non existing",
                LocalDate.of(2026, 12, 8), LocalDateTime.now(), RecurrenceType.NONE,
                null, List.of());

        assertThrows(PersistenceException.class, () -> eventSqlDao.save(event));
    }

    @Test
    void shouldThrowExceptionWhenAssociatingNonExistingTask() {
        TaskSqlDao taskSqlDao = new TaskSqlDao();

        Task savedTask = taskSqlDao.save(new Task("EventSqlDaoTest Deleted task", null));

        taskSqlDao.deleteById(savedTask.getId());

        Event event = new Event("EventSqlDaoTest Invalid task", LocalDate.of(2026, 11, 10));

        event.addTask(savedTask.getId());

        assertThrows(PersistenceException.class, () -> eventSqlDao.save(event));
    }

    @Test
    void shouldDeleteEvent() {
        Event savedEvent = eventSqlDao.save(new Event("EventSqlDaoTest Delete event", LocalDate.of(2026,
                10, 10)));

        eventSqlDao.deleteById(savedEvent.getEventId());

        assertTrue(eventSqlDao.findById(savedEvent.getEventId()).isEmpty());
    }

    @Test
    void shouldDeleteAssociatedTaskAndNoteWhenDeletingEvent() throws SQLException {
        Event savedEvent = eventSqlDao.save(new Event("EventSqlDaoTest Cascade event",
                LocalDate.of(2026, 10, 10)));

        int taskId;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement taskStatement = connection.prepareStatement("""
                 INSERT INTO task (text, priority, status, created_at, event_id)
                 VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)
                 """, Statement.RETURN_GENERATED_KEYS)) {

            taskStatement.setString(1, "EventSqlDaoTest Cascade task");
            taskStatement.setString(2, "MEDIUM");
            taskStatement.setString(3, "PENDING");
            taskStatement.setInt(4, savedEvent.getEventId().value());

            taskStatement.executeUpdate();

            try (ResultSet generatedKeys = taskStatement.getGeneratedKeys()) {
                generatedKeys.next();
                taskId = generatedKeys.getInt(1);
            }
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement noteStatement = connection.prepareStatement("""
                 INSERT INTO note (content, created_at, task_id)
                 VALUES (?, CURRENT_TIMESTAMP, ?)
                 """)) {

            noteStatement.setString(1, "EventSqlDaoTest Cascade note");
            noteStatement.setInt(2, taskId);
            noteStatement.executeUpdate();
        }

        eventSqlDao.deleteById(savedEvent.getEventId());

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 SELECT
                     (SELECT COUNT(*) FROM task WHERE id = ?) AS task_count,
                     (SELECT COUNT(*) FROM note WHERE task_id = ?) AS note_count
                 """)) {

            statement.setInt(1, taskId);
            statement.setInt(2, taskId);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();

                assertEquals(0, resultSet.getInt("task_count"));
                assertEquals(0, resultSet.getInt("note_count"));
            }
        }
    }

    @Test
    void shouldKeepEventAndDeleteNoteWhenDeletingTask() throws SQLException {
        Event savedEvent = eventSqlDao.save(new Event(
                "EventSqlDaoTest Keep event",
                LocalDate.of(2026, 10, 10)
        ));

        int taskId;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement taskStatement = connection.prepareStatement("""
                 INSERT INTO task (text, priority, status, created_at, event_id)
                 VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)
                 """, Statement.RETURN_GENERATED_KEYS)) {

            taskStatement.setString(1, "EventSqlDaoTest Delete task");
            taskStatement.setString(2, "MEDIUM");
            taskStatement.setString(3, "PENDING");
            taskStatement.setInt(4, savedEvent.getEventId().value());

            taskStatement.executeUpdate();

            try (ResultSet generatedKeys = taskStatement.getGeneratedKeys()) {
                generatedKeys.next();
                taskId = generatedKeys.getInt(1);
            }
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO note (content, created_at, task_id)
                 VALUES (?, CURRENT_TIMESTAMP, ?)
                 """)) {

            statement.setString(1, "EventSqlDaoTest Delete task note");
            statement.setInt(2, taskId);
            statement.executeUpdate();
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM task WHERE id = ?")) {

            statement.setInt(1, taskId);
            statement.executeUpdate();
        }

        assertTrue(eventSqlDao.findById(savedEvent.getEventId()).isPresent());

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement("SELECT COUNT(*) FROM note WHERE task_id = ?")) {

            statement.setInt(1, taskId);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();

                assertEquals(0, resultSet.getInt(1));
            }
        }
    }

    @Test
    void shouldReconstructAssociatedTaskIdsWhenFindingAllEvents() throws SQLException {
        Event savedEvent = eventSqlDao.save(new Event("EventSqlDaoTest Find all association",
                LocalDate.of(2026, 10, 10)));

        int taskId;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                 INSERT INTO task (text, priority, status, created_at, event_id)
                 VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)
                 """, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, "EventSqlDaoTest Find all task");
            statement.setString(2, "MEDIUM");
            statement.setString(3, "PENDING");
            statement.setInt(4, savedEvent.getEventId().value());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                taskId = generatedKeys.getInt(1);
            }
        }

        List<Event> events = eventSqlDao.findAll();

        Event foundEvent = events.stream()
                .filter(event -> event.getEventId().equals(savedEvent.getEventId())).findFirst().orElseThrow();

        assertEquals(List.of(new TaskId(taskId)), foundEvent.getTaskIds());
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

    @Test
    void shouldRemoveTaskAssociationWhenSavingEvent() throws SQLException {
        Event savedEvent = eventSqlDao.save(new Event("EventSqlDaoTest Remove association",
                LocalDate.of(2026, 10, 10)));

        int taskId;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
             INSERT INTO task (text, priority, status, created_at, event_id)
             VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)
             """, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, "EventSqlDaoTest Remove task");
            statement.setString(2, "MEDIUM");
            statement.setString(3, "PENDING");
            statement.setInt(4, savedEvent.getEventId().value());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                taskId = generatedKeys.getInt(1);
            }
        }

        Event event = eventSqlDao.findById(savedEvent.getEventId()).orElseThrow();

        event.removeTask(new TaskId(taskId));

        eventSqlDao.save(event);

        Event updatedEvent = eventSqlDao.findById(savedEvent.getEventId()).orElseThrow();

        assertTrue(updatedEvent.getTaskIds().isEmpty());

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
         SELECT event_id
         FROM task
         WHERE id = ?
         """)) {

            statement.setInt(1, taskId);

            try (ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                assertNull(resultSet.getObject("event_id"));
            }
        }
    }

    @Test
    void shouldThrowExceptionWhenSavingNullEvent() {
        assertThrows(IllegalArgumentException.class, () -> eventSqlDao.save(null));
    }

    @Test
    void shouldThrowExceptionWhenFindingByNullEventId() {
        assertThrows(IllegalArgumentException.class, () -> eventSqlDao.findById(null));
    }

    @Test
    void shouldThrowExceptionWhenDeletingByNullEventId() {
        assertThrows(IllegalArgumentException.class, () -> eventSqlDao.deleteById(null));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingEvent() {
        Event savedEvent = eventSqlDao.save(new Event("EventSqlDaoTest Deleted event",
                LocalDate.of(2026, 12, 8)));

        eventSqlDao.deleteById(savedEvent.getEventId());

        assertThrows(PersistenceException.class, () -> eventSqlDao.deleteById(savedEvent.getEventId()));
    }
}

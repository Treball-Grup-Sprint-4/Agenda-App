package com.agenda.infrastructure.sql.dao;

import com.agenda.common.exception.PersistenceException;
import com.agenda.common.persistence.DatabaseConnection;
import com.agenda.event.model.Event;
import com.agenda.event.model.EventId;
import com.agenda.event.model.RecurrenceType;
import com.agenda.event.repository.EventRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventSqlDao implements EventRepository {

    private Event insert(Event event) {
        String sql = """
        INSERT INTO event (text, date, created_at, recurrence, repeat_until)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, event.getText());
            statement.setDate(2, Date.valueOf(event.getEventDate()));
            statement.setTimestamp(3, Timestamp.valueOf(event.getCreatedAt()));
            statement.setString(4, event.getRecurrenceType().name());

            if (event.getRepeatUntil() != null) {
                statement.setDate(5, Date.valueOf(event.getRepeatUntil()));
            } else {
                statement.setNull(5, java.sql.Types.DATE);
            }

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    EventId eventId = new EventId(generatedKeys.getInt(1));

                    return new Event(eventId, event.getText(), event.getEventDate(), event.getCreatedAt(),
                            event.getRecurrenceType(), event.getRepeatUntil(), event.getTaskIds());
                }
            }

            throw new PersistenceException("Failed to retrieve generated Event ID");

        } catch (SQLException e) {
            throw new PersistenceException("Failed to save event", e);
        }
    }

    private Event update(Event event) {
        String sql = """
        UPDATE event
        SET text = ?, date = ?, recurrence = ?, repeat_until = ?
        WHERE id = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, event.getText());
            statement.setDate(2, Date.valueOf(event.getEventDate()));
            statement.setString(3, event.getRecurrenceType().name());

            if (event.getRepeatUntil() != null) {
                statement.setDate(4, Date.valueOf(event.getRepeatUntil()));
            } else {
                statement.setNull(4, java.sql.Types.DATE);
            }

            statement.setInt(5, event.getEventId().value());

            statement.executeUpdate();

            return event;

        } catch (SQLException e) {
            throw new PersistenceException("Failed to update event", e);
        }
    }

    private Event mapEvent(ResultSet resultSet) throws SQLException {
        Date repeatUntil = resultSet.getDate("repeat_until");

        return new Event(new EventId(resultSet.getInt("id")), resultSet.getString("text"),
                resultSet.getDate("date").toLocalDate(),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                RecurrenceType.valueOf(resultSet.getString("recurrence")),
                repeatUntil != null ? repeatUntil.toLocalDate() : null,
                List.of());
    }

    @Override
    public Event save(Event event) {
        if (event.getEventId() == null) {
            return insert(event);
        }

        return update(event);
    }

    @Override
    public Optional<Event> findById(EventId eventId) {
        String sql = """
        SELECT id, text, date, created_at, recurrence, repeat_until
        FROM event
        WHERE id = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, eventId.value());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapEvent(resultSet));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new PersistenceException("Failed to find event by ID", e);
        }
    }

    @Override
    public List<Event> findAll() {
        String sql = """
        SELECT id, text, date, created_at, recurrence, repeat_until
        FROM event
        """;

        List<Event> events = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                events.add(mapEvent(resultSet));
            }

            return events;

        } catch (SQLException e) {
            throw new PersistenceException("Failed to find all events", e);
        }
    }

    @Override
    public void deleteById(EventId eventId) {
        String sql = """
        DELETE FROM event
        WHERE id = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, eventId.value());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenceException("Failed to delete event", e);
        }
    }

}

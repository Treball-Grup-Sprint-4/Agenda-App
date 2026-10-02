package com.agenda.infrastructure.sql.dao;

import com.agenda.common.exception.PersistenceException;
import com.agenda.common.persistence.DatabaseConnection;
import com.agenda.event.model.Event;
import com.agenda.event.model.EventId;
import com.agenda.event.model.RecurrenceType;
import com.agenda.event.repository.EventRepository;
import com.agenda.task.model.TaskId;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class EventSqlDao implements EventRepository {

    private Event insert(Event event) {
        String sql = """
            INSERT INTO event (text, date, created_at, recurrence, repeat_until)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try (PreparedStatement statement =
                         connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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

                    if (!generatedKeys.next()) {
                        throw new SQLException("Failed to retrieve generated Event ID");
                    }

                    EventId eventId = new EventId(generatedKeys.getInt(1));

                    Event savedEvent = new Event(
                            eventId,
                            event.getText(),
                            event.getEventDate(),
                            event.getCreatedAt(),
                            event.getRecurrenceType(),
                            event.getRepeatUntil(),
                            event.getTaskIds()
                    );

                    updateTaskAssociations(connection, savedEvent);

                    connection.commit();

                    return savedEvent;
                }

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

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

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, event.getText());
                statement.setDate(2, Date.valueOf(event.getEventDate()));
                statement.setString(3, event.getRecurrenceType().name());

                if (event.getRepeatUntil() != null) {
                    statement.setDate(4, Date.valueOf(event.getRepeatUntil()));
                } else {
                    statement.setNull(4, java.sql.Types.DATE);
                }

                statement.setInt(5, event.getEventId().value());

                int updatedRows = statement.executeUpdate();

                if (updatedRows == 0) {
                    throw new SQLException("Event not found");
                }

                updateTaskAssociations(connection, event);


                connection.commit();

                return event;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new PersistenceException("Failed to update event", e);
        }
    }

    private void updateTaskAssociations(Connection connection, Event event) throws SQLException {
        String removeSql = """
        UPDATE task
        SET event_id = NULL
        WHERE event_id = ?
        """;

        try (PreparedStatement statement = connection.prepareStatement(removeSql)) {
            statement.setInt(1, event.getEventId().value());
            statement.executeUpdate();
        }

        String addSql = """
        UPDATE task
        SET event_id = ?
        WHERE id = ?
        """;

        try (PreparedStatement statement = connection.prepareStatement(addSql)) {

            List<TaskId> taskIds = event.getTaskIds();

            for (TaskId taskId : taskIds) {
                statement.setInt(1, event.getEventId().value());
                statement.setInt(2, taskId.value());
                statement.addBatch();
            }

            int[] updatedRows = statement.executeBatch();

            for (int rows : updatedRows) {
                if (rows == 0) {
                    throw new SQLException("Task not found");
                }
            }
        }
    }

    private List<TaskId> findTaskIds(Connection connection, EventId eventId) throws SQLException {
        String sql = """
            SELECT id
            FROM task
            WHERE event_id = ?
            """;

        List<TaskId> taskIds = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, eventId.value());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    taskIds.add(new TaskId(resultSet.getInt("id")));
                }
            }
        }

        return taskIds;
    }

    private Event mapEvent(Connection connection, ResultSet resultSet) throws SQLException {
        Date repeatUntil = resultSet.getDate("repeat_until");
        EventId eventId = new EventId(resultSet.getInt("id"));

        return new Event(eventId, resultSet.getString("text"),
                resultSet.getDate("date").toLocalDate(),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                RecurrenceType.valueOf(resultSet.getString("recurrence")),
                repeatUntil != null ? repeatUntil.toLocalDate() : null, findTaskIds(connection, eventId));
    }

    @Override
    public Event save(Event event) {

        if (event == null) {
            throw new IllegalArgumentException("Event must not be NULL");
        }

        if (event.getEventId() == null) {
            return insert(event);
        }

        return update(event);
    }

    @Override
    public Optional<Event> findById(EventId eventId) {

        if (eventId == null) {
            throw new IllegalArgumentException("Event ID must not be NULL");
        }

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
                    return Optional.of(mapEvent(connection, resultSet));
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
            SELECT
                e.id AS event_id,
                e.text,
                e.date,
                e.created_at,
                e.recurrence,
                e.repeat_until,
                t.id AS task_id
            FROM event e
            LEFT JOIN task t ON t.event_id = e.id
            ORDER BY e.id
            """;

        Map<EventId, Event> events = new LinkedHashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                EventId eventId = new EventId(resultSet.getInt("event_id"));

                Event event = events.get(eventId);

                if (event == null) {
                    Date repeatUntil = resultSet.getDate("repeat_until");

                    event = new Event(eventId, resultSet.getString("text"),
                            resultSet.getDate("date").toLocalDate(),
                            resultSet.getTimestamp("created_at").toLocalDateTime(),
                            RecurrenceType.valueOf(resultSet.getString("recurrence")),
                            repeatUntil != null ? repeatUntil.toLocalDate() : null, List.of());

                    events.put(eventId, event);
                }

                int taskId = resultSet.getInt("task_id");

                if (!resultSet.wasNull()) {
                    event.addTask(new TaskId(taskId));
                }
            }

            return new ArrayList<>(events.values());

        } catch (SQLException e) {
            throw new PersistenceException("Failed to find all events", e);
        }
    }

    @Override
    public void deleteById(EventId eventId) {

        if (eventId == null) {
            throw new IllegalArgumentException("Event ID must not be NULL");
        }

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

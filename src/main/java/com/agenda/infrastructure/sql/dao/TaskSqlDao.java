package com.agenda.infrastructure.sql.dao;

import com.agenda.common.exception.PersistenceException;
import com.agenda.common.persistence.DatabaseConnection;
import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;
import com.agenda.task.model.TaskPriority;
import com.agenda.task.model.TaskStatus;
import com.agenda.task.repository.TaskRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskSqlDao implements TaskRepository {

    @Override
    public Task save(Task task) {
        if(task == null) {
            throw new IllegalArgumentException("Task must not be NULL");
        }

        if(task.getId() == null) {
            return insert(task);
        }

        return update(task);
    }

   private Task insert(Task task) {
        String sql = """
                INSERT INTO task (text, priority, status, expiration_date, completed_at, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, task.getText());
            statement.setString(2, task.getPriority().name());
            statement.setString(3, task.getStatus().name());

            if(task.getExpirationDate() == null) {
                statement.setNull(4, Types.DATE);
            } else {
                statement.setDate(4, Date.valueOf(task.getExpirationDate()));
            }

            if(task.getCompletedAt() == null) {
                statement.setNull(5, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(5, Timestamp.valueOf(task.getCompletedAt()));
            }

            statement.setTimestamp(6, Timestamp.valueOf(task.getCreatedAt()));

            statement.executeUpdate();

            try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {

                    return new Task(
                            new TaskId(generatedKeys.getInt(1)), task.getText(), task.getPriority(),
                            task.getStatus(), task.getExpirationDate(), task.getCreatedAt(), task.getCompletedAt());
                }
            }

            throw new PersistenceException("Error fetching task ID");

        } catch(SQLException e) {
            throw new PersistenceException("Error saving task", e);
       }
    }

    private Task update(Task task) {
        String sql = """
                UPDATE task
                SET text = ?, priority = ?, status = ?, expiration_date = ?, completed_at = ?
                WHERE id = ?
                """;

        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.getText());
            statement.setString(2, task.getPriority().name());
            statement.setString(3, task.getStatus().name());

            if(task.getExpirationDate() == null) {
                statement.setNull(4, Types.DATE);
            } else {
                statement.setDate(4, Date.valueOf(task.getExpirationDate()));
            }
            if(task.getCompletedAt() == null) {
                statement.setNull(5, Types.TIMESTAMP );
            } else {
                statement.setTimestamp(5, Timestamp.valueOf(task.getCompletedAt()));
            }

            statement.setInt(6, task.getId().value());

            int updateRows = statement.executeUpdate();

            if(updateRows == 0) {
                throw new PersistenceException(String.format("Task with ID %d not found", task.getId().value()));
            }
            return task;

        } catch (SQLException e) {
            throw  new PersistenceException("Error updating Task", e);
        }
    }

    @Override
    public List<Task> findAll() {
        String sql = """
                SELECT id, text, priority, status, expiration_date, created_at, completed_at
                FROM task
                """;

        List<Task> tasks = new ArrayList<>();

        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {

            while(resultSet.next()){
                tasks.add(mapTask(resultSet));
            }
            return tasks;
        } catch (SQLException e) {
            throw new PersistenceException("Error finding tasks", e);
        }
    }

    private Task mapTask(ResultSet resultSet) throws  SQLException {
        Date expirationDate = resultSet.getDate("expiration_date");
        Timestamp completedAt = resultSet.getTimestamp("completed_at");

        return new Task(new TaskId(resultSet.getInt("id")), resultSet.getString("text"),
                TaskPriority.valueOf(resultSet.getString("priority")),
                TaskStatus.valueOf(resultSet.getString("status")),
                expirationDate == null ? null : expirationDate.toLocalDate(),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                completedAt == null ? null : completedAt.toLocalDateTime());
    }

    @Override
    public Optional<Task> findById(TaskId id) {
        if(id == null) {
            throw new IllegalArgumentException("Task ID must not be NULL");
        }

        String sql = """
                SELECT id, text, priority, status, expiration_date, created_at, completed_at
                FROM task
                WHERE id = ?
                """;

        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id.value());

            try(ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    return Optional.of(mapTask(resultSet));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new PersistenceException("Error finding task", e);
        }


    }

    @Override
    public void deleteById(TaskId id) {
        if(id == null) {
            throw new IllegalArgumentException("Task ID must not be NULL");
        }

        String sql = """
                DELETE FROM task
                WHERE id = ?
                """;

        try(Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id.value());

            int updatedRows = statement.executeUpdate();

            if(updatedRows == 0) {
                throw new PersistenceException(String.format("Task with ID %d not found", id.value()));
            }

        } catch(SQLException e) {
            throw new PersistenceException("Error deleting task", e);
        }
    }
}

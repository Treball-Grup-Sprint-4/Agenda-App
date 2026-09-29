/*package com.agenda.infrastructure.sql.dao;

import com.agenda.common.persistence.DatabaseConnection;
import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;
import com.agenda.task.repository.TaskRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

public class TaskSqlDao implements TaskRepository {

*//*    @Override
    public Task save(Task task) {
        if(task == null) {
            throw new IllegalArgumentException("Task must not be NULL");
        }

        if(task.getId() == null) {
            return;
        }

        return null;
    }*//*

   *//* private Task insert(Task task) {
        String sql = """
                INSERT INTO task (text, priority, status, expiration_date, completed_at, event_id, created_at)
                VALUES ((?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            fillCommonFields(statement, task);
        }
    }*//*

    @Override
    public List<Task> findAll() {
        return List.of();
    }

    @Override
    public Optional<Task> findById(TaskId id) {
        return Optional.empty();
    }

    @Override
    public void deleteById(TaskId id) {

    }
}*/

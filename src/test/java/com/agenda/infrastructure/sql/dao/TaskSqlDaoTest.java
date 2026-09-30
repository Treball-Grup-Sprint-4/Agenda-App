package com.agenda.infrastructure.sql.dao;

import com.agenda.common.persistence.DatabaseConnection;
import com.agenda.event.model.EventId;
import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class TaskSqlDaoTest {

    private TaskSqlDao sut;
    private TaskId savedTaskId;


    @BeforeEach
    void setup() {
        sut = new TaskSqlDao();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if(savedTaskId == null) {
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM note WHERE task_id = ?")) {
                statement.setInt(1, savedTaskId.value());
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM task WHERE id = ?")) {
                statement.setInt(1, savedTaskId.value());
                statement.executeUpdate();
            }
        }
    }

    @Test
    void saveShouldSaveNewTask() {

        Task testTask = new Task("Default Text", null);

        Task savedTask = sut.save(testTask);

        assertNotNull(savedTask.getId());
        assertEquals("Default Text", savedTask.getText());


    }

    @Test
    void findAllShouldReturnAllTasks() {
    }

    @Test
    void findById() {
    }

    @Test
    void deleteById() {
    }
}
package com.agenda.infrastructure.sql.dao;

import com.agenda.common.persistence.DatabaseConnection;
import com.agenda.event.model.EventId;
import com.agenda.note.model.Note;
import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TaskSqlDaoTest {

    private TaskSqlDao sut;
    private TaskId savedTaskId;
    private List<TaskId> savedTasksIds;


    @BeforeEach
    void setup() {
        sut = new TaskSqlDao();
        savedTasksIds = new ArrayList<>();
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
        Task firstTask = sut.save(new Task("First task's text", null));
        Task secondTask = sut.save(new Task("Second task's text", null));

        List<Task> tasks = sut.findAll();

        assertTrue(tasks
                .stream()
                .anyMatch(task -> task.getId().equals(firstTask.getId())));

        assertTrue(tasks
                .stream()
                .anyMatch(note -> note.getId().equals(secondTask.getId())));
    }

    @Test
    void findByIdShouldReturnExpectedTasks() {
        Task savedTask = sut.save(new Task("Default text", null));

        savedTasksIds.add(savedTask.getId());

        Optional<Task> task = sut.findById(savedTask.getId());

        assertTrue(task.isPresent());
        assertEquals(savedTask.getId(), task.get().getId());
        assertEquals("Default text", task.get().getText());

    }

    @Test
    void deleteByIdShouldReturnEmptyAfterSearchingById() {

        Task savedTask = sut.save(new Task("Default Text", null));

        sut.deleteById(savedTask.getId());

        assertTrue(sut.findById(savedTask.getId()).isEmpty());
    }
}
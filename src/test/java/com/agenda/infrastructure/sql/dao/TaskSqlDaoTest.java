package com.agenda.infrastructure.sql.dao;

import com.agenda.common.persistence.DatabaseConnection;
import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.agenda.event.model.Event;
import com.agenda.common.exception.PersistenceException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskSqlDaoTest {

    private TaskSqlDao sut;
    private List<TaskId> savedTaskIds;


    @BeforeEach
    void setup() {
        sut = new TaskSqlDao();
        savedTaskIds = new ArrayList<>();
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM task WHERE id = ?")) {

            for (TaskId taskId : savedTaskIds) {
                statement.setInt(1, taskId.value());
                statement.addBatch();
            }

            statement.executeBatch();

            try (PreparedStatement eventStatement = connection.prepareStatement(
                    "DELETE FROM event WHERE text LIKE 'TaskSqlDaoTest%'")) {
                eventStatement.executeUpdate();
            }
        }
    }

    @Test
    void saveShouldSaveNewTask() {

        Task testTask = new Task("TaskSqlDaoTest Default Text", null);

        Task savedTask = sut.save(testTask);
        savedTaskIds.add(savedTask.getId());

        assertNotNull(savedTask.getId());
        assertEquals("TaskSqlDaoTest Default Text", savedTask.getText());
    }

    @Test
    void shouldUpdateTask() {
        Task savedTask = sut.save(new Task("TaskSqlDaoTest Original", null));
        savedTaskIds.add(savedTask.getId());

        savedTask.updateDetails("TaskSqlDaoTest Updated");
        sut.save(savedTask);

        Task updatedTask = sut.findById(savedTask.getId()).orElseThrow();

        assertEquals("TaskSqlDaoTest Updated", updatedTask.getText());
    }

    @Test
    void findAllShouldReturnAllTasks() {
        Task firstTask = sut.save(new Task("TaskSqlDaoTest First task's text", null));
        Task secondTask = sut.save(new Task("TaskSqlDaoTest Second task's text", null));

        savedTaskIds.add(firstTask.getId());
        savedTaskIds.add(secondTask.getId());

        List<Task> tasks = sut.findAll();

        assertTrue(tasks.stream().anyMatch(task -> task.getId().equals(firstTask.getId())));

        assertTrue(tasks.stream().anyMatch(note -> note.getId().equals(secondTask.getId())));
    }

    @Test
    void findByIdShouldReturnExpectedTasks() {
        Task savedTask = sut.save(new Task("TaskSqlDaoTest Default text", null));

        savedTaskIds.add(savedTask.getId());

        Optional<Task> task = sut.findById(savedTask.getId());

        assertTrue(task.isPresent());
        assertEquals(savedTask.getId(), task.get().getId());
        assertEquals("TaskSqlDaoTest Default text", task.get().getText());

    }

    @Test
    void deleteByIdShouldReturnEmptyAfterSearchingById() {

        Task savedTask = sut.save(new Task("TaskSqlDaoTest Default Text", null));

        savedTaskIds.add(savedTask.getId());

        sut.deleteById(savedTask.getId());

        assertTrue(sut.findById(savedTask.getId()).isEmpty());
    }

    @Test
    void shouldKeepEventAssociationWhenUpdatingTask() {
        Task savedTask = sut.save(new Task("TaskSqlDaoTest Original", null));
        savedTaskIds.add(savedTask.getId());

        EventSqlDao eventSqlDao = new EventSqlDao();

        Event savedEvent = eventSqlDao.save(new Event("TaskSqlDaoTest Event",
                LocalDate.now().plusDays(1)));

        savedEvent.addTask(savedTask.getId());
        eventSqlDao.save(savedEvent);

        savedTask.updateDetails("TaskSqlDaoTest Updated");
        sut.save(savedTask);

        Event updatedEvent = eventSqlDao.findById(savedEvent.getEventId()).orElseThrow();

        assertEquals(List.of(savedTask.getId()), updatedEvent.getTaskIds());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingTask() {
        assertThrows(PersistenceException.class, () -> sut.deleteById(new TaskId(997)));
    }

    @Test
    void shouldThrowExceptionWhenSavingNullTask() {
        assertThrows(IllegalArgumentException.class, () -> sut.save(null));
    }

    @Test
    void shouldThrowExceptionWhenFindingByNullTaskId() {
        assertThrows(IllegalArgumentException.class, () -> sut.findById(null));
    }

    @Test
    void shouldThrowExceptionWhenDeletingByNullTaskId() {
        assertThrows(IllegalArgumentException.class, () -> sut.deleteById(null));
    }
}
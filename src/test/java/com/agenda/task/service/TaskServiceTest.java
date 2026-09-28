package com.agenda.task.service;


import com.agenda.common.exception.TaskNotFoundException;
import com.agenda.task.dto.TaskDto;
import com.agenda.task.model.*;
import com.agenda.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private FakeTaskRepository fakeTaskRepository;
    private TaskService sut;

    @BeforeEach
    void setup() {
        fakeTaskRepository = new FakeTaskRepository();
        sut = new TaskService(fakeTaskRepository);
    }

    private static class FakeTaskRepository implements TaskRepository {

        private final List<Task> taskList = new ArrayList<>();
        private int nextId = 1;

        @Override
        public Task save(Task task) {
            if (task == null) {
                throw new IllegalArgumentException("Task must not be NULL");
            }

            if(task.getId() == null) {
                Task savedTask = task.addId(new TaskId(nextId++));
                this.taskList.add(savedTask);
                return savedTask;
            }

            int pos = this.taskList.indexOf(task);
            if (pos >= 0) {
                this.taskList.set(pos, task);
            } else {
                this.taskList.add(task);
            }
            return task;
        }

        @Override
        public List<Task> findAll() {
            return List.copyOf(this.taskList);
        }

        @Override
        public Optional<Task> findById(TaskId id) {
            if (id == null) {
                throw new IllegalArgumentException("Task ID must not be NULL");
            }

            return this.taskList
                    .stream()
                    .filter(task -> id.equals(task.getId()))
                    .findFirst();
        }

        @Override
        public void deleteById(TaskId id) {
            if (id == null) {
                throw new IllegalArgumentException("Task ID must not be NULL");
            }
            this.taskList.removeIf(task -> id.equals(task.getId()));
        }

    }


    TaskDto testTaskDto(String text, TaskPriority priority, LocalDate expirationDate){
        return new TaskDto(null, text, priority, null, expirationDate,
                null, null, null);
    }

    LocalDate expirationDate = LocalDate.now().plusMonths(2);

    @Test

    void createShouldInitializeFieldsWithExpectedValues() {


        TaskDto task = sut.create(testTaskDto("Default text", TaskPriority.HIGH, expirationDate));

        assertEquals(1, task.id().value());
        assertEquals("Default text", task.text());
        assertEquals(TaskPriority.HIGH, task.priority());
        assertEquals(TaskStatus.PENDING, task.status());
        assertNotNull(task.createdAt());
        assertNull(task.completedAt());
        assertEquals(expirationDate, task.expirationDate());
    }

    @Test
    void updateShouldThrowExceptionWhenUpdatingNonExistingTask() {
        TaskId nonExistingId = new TaskId(38);
        TaskDto dto = testTaskDto("New text", TaskPriority.LOW, expirationDate);

        assertThrows(TaskNotFoundException.class, ()-> sut.update(nonExistingId, dto));

    }

    @Test
    void deleteShouldRemoveExistingTask() {
        TaskDto createdTaskDto = sut.create(testTaskDto("Another text", TaskPriority.MEDIUM, expirationDate));

        assertFalse(sut.findAll().isEmpty());

        sut.delete(createdTaskDto.id());

        assertTrue(sut.findAll().isEmpty());

    }

    @Test
    void deleteShouldThrowExceptionWhenDeletingNonExistingTask() {
        sut.create(testTaskDto("Default", TaskPriority.MEDIUM, expirationDate));
        assertThrows(TaskNotFoundException.class, ()-> sut.delete(new TaskId(83)));

    }

    @Test
    void completeShouldSetTaskStatusToCompleted() {
        TaskDto created = sut.create(testTaskDto("Default02", null, expirationDate));

        sut.complete(created.id());

        TaskDto completed = sut.findAll().getFirst();

        assertEquals(TaskStatus.COMPLETED, completed.status());
        assertNotNull(completed.completedAt());
    }

    @Test
    void findAllShouldReturnExpectedList() {
        sut.create(testTaskDto("FirstText", TaskPriority.HIGH, null));
        sut.create(testTaskDto("SecondText", null , expirationDate));

        List<TaskDto> tasks = sut.findAll();

        assertEquals(2, tasks.size());
        assertEquals("FirstText", tasks.get(0).text());
        assertEquals(expirationDate, tasks.get(1).expirationDate());
    }

    @Test
    void findPendingShouldReturnExpectedList() {
        TaskDto first = sut.create(testTaskDto("Pending", null, null));
        TaskDto second = sut.create(testTaskDto("Complete", null, null));

        sut.complete(second.id());

        List<TaskDto> pending = sut.findPending();

        assertEquals(first.id(), pending.getFirst().id());
    }

    @Test
    void findCompletedShouldReturnExpectedList() {

        sut.create(testTaskDto("Pending02", null, null));
        TaskDto second = sut.create(testTaskDto("Completed02", null, null));

        sut.complete(second.id());

        List<TaskDto> completed = sut.findCompleted();

        assertEquals(second.id(), completed.getFirst().id());
    }

    @Test
    void filterByPriorityShouldReturnExpectedList() {
        sut.create(testTaskDto("High", TaskPriority.HIGH, null));
        sut.create(testTaskDto("Low", TaskPriority.LOW, null));

        List<TaskDto> high = sut.filterByPriority(TaskPriority.HIGH);

        assertEquals("High", high.getFirst().text());
    }

    @Test
    void filterByStatusShouldReturnExpectedList() {
        sut.create(testTaskDto("Default Text 01", null, null));

        TaskDto done = sut.create(testTaskDto("Default Text 02", null, null));

        sut.complete(done.id());

        List<TaskDto> completed = sut.filterByStatus(TaskStatus.COMPLETED);

        assertEquals(1, completed.size());
        assertEquals(done.id(), completed.getFirst().id());
    }

    @Test
    void filterByDateShouldReturnExpectedList() {
        LocalDate date01 = LocalDate.now().plusDays(5);
        LocalDate date02 = LocalDate.now().plusMonths(2);

        sut.create(testTaskDto("Target date", null, date01));
        sut.create(testTaskDto("Left out date", null, date02));

        List<TaskDto> filtered = sut.filterByDate(date01);

        assertEquals(1, filtered.size());
        assertEquals("Target date", filtered.get(0).text());
    }

    @Test
    void sortTasksShouldReturnListInExpectedOrder() {
        sut.create(testTaskDto("High", TaskPriority.HIGH, null));
        sut.create(testTaskDto("Low", TaskPriority.LOW, null));
        sut.create(testTaskDto("Medium", TaskPriority.MEDIUM, null));

        List<TaskDto> sorted = sut.sortTasks(new PrioritySortStrategy());

        assertEquals("Low", sorted.get(0).text());
        assertEquals("Medium", sorted.get(1).text());
        assertEquals("High", sorted.get(2).text());
    }

    }


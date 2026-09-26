package com.agenda.task.repository;

import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TaskInMemoryRepositoryTest {
    TaskRepository sut = new TaskInMemoryRepository();

    @Test
    void newlyCreatedRepositoryShouldHaveListWithZeroElements() {
        assertEquals(0, sut.findAll().size());
    }

    @Test
    void saveShouldIncreaseTheListByOne() {
        int initialNumberOfElements = sut.findAll().size();
        int expectedNumberOfElements = initialNumberOfElements + 1;
        Task tesTask = new Task("DefaultText", LocalDate.of(2026, Month.OCTOBER, 3));

        sut.save(tesTask);
        assertEquals(expectedNumberOfElements, sut.findAll().size());
    }

    @Test
    void findAllShouldReturnExpectedList() {
        Task tesTask = new Task("DefaultText", LocalDate.of(2026, Month.OCTOBER, 3));
        sut.save(tesTask);
        List expectedList = List.of(tesTask);
        assertThat(expectedList).usingRecursiveComparison().isEqualTo(sut.findAll());
    }


    @Test
    void findByIdShouldReturnExpectedValue() {

        TaskId testTaskId = new TaskId(2);

        Task testTask = new Task("DefaultText", LocalDate.of(2026, Month.OCTOBER, 3))
                .addId(testTaskId);

        sut.save(testTask);

        Optional<Task> foundTask = sut.findById(testTaskId);

        assertTrue(foundTask.isPresent());
        assertEquals(testTask, foundTask.get());
    }

    @Test
    void deleteByIdShouldDecreaseListByOne() {
        TaskId testTaskId = new TaskId(2);
        TaskId notAddedTaskId = new TaskId(3);

        sut.save(new Task("DefaultText", LocalDate.of(2026, Month.OCTOBER, 3))
                .addId(testTaskId));

        int expectedListSize = sut.findAll().size() - 1;

        sut.deleteById(notAddedTaskId);
        assertNotEquals(expectedListSize, sut.findAll().size());

        sut.deleteById(testTaskId);
        assertEquals(expectedListSize, sut.findAll().size());

    }

    @Test
    void deleteByIdShouldEliminateElementFromList() {

        TaskId testTaskId = new TaskId(2);

        Task testTask = new Task("DefaultText", LocalDate.of(2026, Month.OCTOBER, 3))
                .addId(testTaskId);

        sut.save(testTask);

        assertTrue(sut.findAll().contains(testTask));

        sut.deleteById(testTaskId);

        assertFalse(sut.findAll().contains(testTask));
    }
}
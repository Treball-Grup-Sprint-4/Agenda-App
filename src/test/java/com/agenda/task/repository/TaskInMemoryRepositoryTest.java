package com.agenda.task.repository;

import com.agenda.task.model.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

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
    }

    @Test
    void findByIdShouldReturnExpectedValue() {
    }

    @Test
    void deleteByIdShouldDecreaseListByOne() {
    }

    @Test
    void deleteByIdShouldEliminateElementFromList() {

    }
}
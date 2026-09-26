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
    }

    @Test
    void deleteByIdShouldDecreaseListByOne() {
    }

    @Test
    void deleteByIdShouldEliminateElementFromList() {

    }
}
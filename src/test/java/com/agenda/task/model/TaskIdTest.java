package com.agenda.task.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskIdTest {

    @Test
    void shouldCreateTaskIdWhenValueIsPositive() {
        TaskId taskId = new TaskId(1);

        assertEquals(1, taskId.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsZero() {
        assertThrows(IllegalArgumentException.class, () -> new TaskId(0));
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new TaskId(-1));
    }
}
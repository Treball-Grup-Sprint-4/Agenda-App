package com.agenda.task.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldCreateTaskWithoutExpirationDate() {
        Task task = new Task("Limpiar", null);

        assertNull(task.getExpirationDate());
    }

    @Test
    void shouldThrowExceptionWhenExpirationDateIsInThePast() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        assertThrows(IllegalArgumentException.class, () -> new Task("Desayuno", yesterday));
    }

    @Test
    void shouldCreateTaskWhenExpirationDateIsToday() {
        LocalDate today = LocalDate.now();

        Task task = new Task("Limpiar coche", today);

        assertEquals(today, task.getExpirationDate());
    }

    @Test
    void shouldThrowExceptionWhenTextExceeds256Characters() {
        String text = "a".repeat(257);

        assertThrows(IllegalArgumentException.class, () -> new Task(text, null));
    }

    @Test
    void shouldCreateTaskWhenTextHasExactly256Characters() {
        String text = "a".repeat(256);

        Task task = new Task(text, null);

        assertEquals(text, task.getText());
    }

    @Test
    void shouldThrowExceptionWhenPriorityIsNull() {
        Task task = new Task("Comida", null);

        assertThrows(IllegalArgumentException.class, () -> task.updateDetails((TaskPriority) null));
    }

    @Test
    void shouldCreateTaskWithMediumPriorityByDefault() {
        Task task = new Task("Arreglar coche", null);

        assertEquals(TaskPriority.MEDIUM, task.getPriority());
    }

    @Test
    void shouldCreateTaskWithPendingStatusByDefault() {
        Task task = new Task("Examen", null);

        assertEquals(TaskStatus.PENDING, task.getStatus());
    }

    @Test
    void shouldCreateTaskWithCompletedAtNull() {
        Task task = new Task("Comprar agua", null);

        assertNull(task.getCompletedAt());
    }

    @Test
    void shouldMarkTaskAsCompleted() {
        Task task = new Task("Pintar puerta", null);

        task.markAsCompleted();

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertNotNull(task.getCompletedAt());
    }

    @Test
    void shouldNotChangeCompletedAtWhenTaskIsAlreadyCompleted() {
        Task task = new Task("Preparar tuper", null);

        task.markAsCompleted();
        LocalDateTime firstCompletedAt = task.getCompletedAt();

        task.markAsCompleted();

        assertEquals(firstCompletedAt, task.getCompletedAt());
    }

    @Test
    void shouldThrowExceptionWhenTextIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null, null));
    }

    @Test
    void shouldThrowExceptionWhenTextIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Task(" ", null));
    }

    @Test
    void shouldCreateTaskWithCreatedAt() {
        Task task = new Task("Gimnasia", null);

        assertNotNull(task.getCreatedAt());
    }

    @Test
    void shouldUpdateTaskText() {
        Task task = new Task("Preparar Verdura", null);

        task.updateDetails("Preparar Macarrones");

        assertEquals("Preparar Macarrones", task.getText());
    }

    @Test
    void shouldThrowExceptionWhenUpdatedTextExceeds256Characters() {
        Task task = new Task("H", null);
        String text = "a".repeat(257);

        assertThrows(IllegalArgumentException.class, () -> task.updateDetails(text));
    }

    @Test
    void shouldUpdateTaskPriority() {
        Task task = new Task("Pintar paredes de casa", null);

        task.updateDetails(TaskPriority.HIGH);

        assertEquals(TaskPriority.HIGH, task.getPriority());
    }

    @Test
    void shouldUpdateExpirationDate() {
        Task task = new Task("Entregar trabajo", null);
        LocalDate newDate = LocalDate.now().plusDays(7);

        task.updateDetails(newDate);

        assertEquals(newDate, task.getExpirationDate());
    }

    @Test
    void shouldRemoveExpirationDate() {
        Task task = new Task("Recoger ropa", LocalDate.now().plusDays(7));

        task.updateDetails((LocalDate) null);

        assertNull(task.getExpirationDate());
    }

    @Test
    void shouldThrowExceptionWhenUpdatedExpirationDateIsInThePast() {
        Task task = new Task("Comprar periodico", null);
        LocalDate yesterday = LocalDate.now().minusDays(1);

        assertThrows(IllegalArgumentException.class, () -> task.updateDetails(yesterday));
    }

    @Test
    void shouldUpdateAllTaskDetails() {
        Task task = new Task("Comprar tomate", null);
        LocalDate newDate = LocalDate.now().plusDays(7);

        task.updateDetails("Comprar lechuga", TaskPriority.LOW, newDate);

        assertEquals("Comprar lechuga", task.getText());
        assertEquals(TaskPriority.LOW, task.getPriority());
        assertEquals(newDate, task.getExpirationDate());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAllDetailsWithNullPriority() {
        Task task = new Task("Siesta", null);
        LocalDate newDate = LocalDate.now().plusDays(7);

        assertThrows(IllegalArgumentException.class, () ->
                task.updateDetails("Siesta", null, newDate));
    }

    @Test
    void shouldCreateTaskWithoutEvent() {
        Task task = new Task("Lavar ropa", null);

        assertNull(task.getEventId());
    }


}
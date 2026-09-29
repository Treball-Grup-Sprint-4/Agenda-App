package com.agenda.task.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private final TaskId id;
    private String text;
    private TaskPriority priority;
    private TaskStatus status;
    private final LocalDateTime createdAt;
    private LocalDate expirationDate;
    private LocalDateTime completedAt;

    public Task(String text, LocalDate expirationDate) {
        checkInputData(text, expirationDate);

        this.id = null;
        this.text = text;
        this.priority = TaskPriority.MEDIUM;
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.expirationDate = expirationDate;
        this.completedAt = null;
    }

    public Task(TaskId id, String text, TaskPriority priority, TaskStatus status, LocalDate expirationDate,
            LocalDateTime createdAt, LocalDateTime completedAt) {

        checkInputText(text);
        checkInputPriority(priority);

        if (id == null) {
            throw new IllegalArgumentException("Task ID must not be NULL");
        }

        if (status == null) {
            throw new IllegalArgumentException("Status must not be NULL");
        }

        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt must not be NULL");
        }

        this.id = id;
        this.text = text;
        this.priority = priority;
        this.status = status;
        this.expirationDate = expirationDate;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    private static void checkInputData(String text, LocalDate expirationDate) {
        checkInputText(text);
        checkInputDate(expirationDate);
    }

    private static void checkInputText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text must not be NULL");
        }

        if (text.isBlank()) {
            throw new IllegalArgumentException("Text must not be empty");
        }

        if (text.length() > 256) {
            throw new IllegalArgumentException("Text must not exceed 256 characters");
        }
    }

    private static void checkInputDate(LocalDate date) {

        if (date != null && date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiration date must not be prior current date");
        }
    }

    private static void checkInputPriority(TaskPriority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Priority must not be NULL");
        }
    }

    public TaskId getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public TaskPriority getPriority() {
        return this.priority;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDate getExpirationDate() {
        return this.expirationDate;
    }

    public LocalDateTime getCompletedAt() {
        return this.completedAt;
    }

    public void markAsCompleted() {
        if (this.getStatus() == TaskStatus.COMPLETED) {
            return;
        }
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    /*
    Using method overload for flexibility, allowing details to be updated depending on the
    input parameters.
     */
    public void updateDetails(String text, TaskPriority priority, LocalDate expirationDate) {
        checkInputData(text, expirationDate);
        checkInputPriority(priority);

        this.text = text;
        this.priority = priority;
        this.expirationDate = expirationDate;
    }

    public void updateDetails(String text) {
        checkInputText(text);
        this.text = text;
    }

    public void updateDetails(TaskPriority priority) {
        checkInputPriority(priority);
        this.priority = priority;
    }

    public void updateDetails(LocalDate expirationDate) {
        checkInputDate(expirationDate);
        this.expirationDate = expirationDate;
    }
}

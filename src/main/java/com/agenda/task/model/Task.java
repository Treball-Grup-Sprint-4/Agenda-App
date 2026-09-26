package com.agenda.task.model;

import com.agenda.event.model.EventId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


public class Task {
    private final TaskId id;
    private String text;
    private TaskPriority priority;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDate expirationDate;
    private LocalDateTime completedAt;
    private EventId eventId;

    public Task(String text, LocalDate expirationDate) {
        checkInputData(text, expirationDate);

        this.id = null;
        this.text = text;
        this.expirationDate = expirationDate;

        this.priority = TaskPriority.MEDIUM;
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.completedAt = null;
        this.eventId = null;
    }

    /*
    Private constructor used by addId(), to customize an ID of a non-persisted Task
     */
    private Task(TaskId id, Task sourceTask) {
        this.id = id;
        this.text = sourceTask.text;
        this.expirationDate = sourceTask.expirationDate;

        this.priority = sourceTask.priority;
        this.status = sourceTask.status;
        this.createdAt = sourceTask.createdAt;
        this.completedAt = sourceTask.completedAt;
        this.eventId = sourceTask.eventId;
    }

    private static void checkInputData(String text, LocalDate expirationDate) {
        checkInputText(text);
        checkInputDate(expirationDate);
    }

    private static void checkInputText(String text) {
        if(text == null) {
            throw new IllegalArgumentException("Text must not be NULL");
        }

        if(text.isBlank()) {
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

    public EventId getEventId() {
        return this.eventId;
    }

    public void setEventId(EventId id) {
        this.eventId = id;
    }

    public void markAsCompleted() {
        if(this.getStatus() == TaskStatus.COMPLETED) {
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

    /*
        Returns a Task with customized ID. Ensures an existent ID is not swaped by a new ID.
        Only updates ID if the current ID is NULL, and it only accepts a valid ID. Doesn't
        allow modification of persisted instances.
     */
    public Task addId(TaskId id) {
        if(id == null) {
            throw new IllegalArgumentException("Input ID must not be NULL");
        }
        if(this.id != null) {
            throw new IllegalStateException("The task ID must be NULL");
        }
        return new Task(id, this);
    }

    @Override
    public boolean equals(Object o) {

        if(this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return this.id != null && Objects.equals(this.id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}

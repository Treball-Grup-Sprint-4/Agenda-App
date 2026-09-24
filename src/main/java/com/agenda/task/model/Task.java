package com.agenda.task.model;

import com.agenda.event.model.EventId;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private TaskId id;
    private String text;
    private TaskPriority priority;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDate expirationDate;
    private LocalDateTime completedAt;
    private EventId eventId;


    public Task(String text, LocalDate expirationDate) {

        checkInputData(text);

        this.text = text;
        this.expirationDate = expirationDate;

        priority = TaskPriority.MEDIUM;
        status = TaskStatus.PENDING;
        createdAt = LocalDateTime.now();
        completedAt = null;
        eventId = null;
    }

    private static void checkInputData(String text) {
        if(text == null) {
            throw new IllegalArgumentException("Text must not be NULL");
        }

        if(text.isBlank()) {
            throw new IllegalArgumentException("Text must not be empty");
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

    public void setId(TaskId id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
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
}

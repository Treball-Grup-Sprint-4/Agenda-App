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

        checkInputData(text, expirationDate);

        this.text = text;
        this.expirationDate = expirationDate;

        priority = TaskPriority.MEDIUM;
        status = TaskStatus.PENDING;
        createdAt = LocalDateTime.now();
        completedAt = null;
        eventId = null;
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
    }

    private static void checkInputDate(LocalDate date) {

        if(date == null) {
            throw new IllegalArgumentException("Date must not be NULL");
        }

        if(date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiration date must not be prior current date");
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

    /*
    Using method overload for flexibility, allowing details to be updated depending on the
    input parameters.
     */

    public void updateDetails(String text, TaskPriority priority, LocalDate expirationDate) {

        checkInputData(text, expirationDate);

        this.text = text;
        this.priority = priority;
        this.expirationDate = expirationDate;
    }

    public void updateDetails(String text) {
        checkInputText(text);
        this.text = text;
    }

    public void updateDetails(TaskPriority priority) {
        this.priority = priority;
    }
    
    public void updateDetails(LocalDate expirationDate) {
        checkInputDate(expirationDate);
        this.expirationDate = expirationDate;
    }
}

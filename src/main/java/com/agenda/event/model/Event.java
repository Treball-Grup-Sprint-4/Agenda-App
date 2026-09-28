package com.agenda.event.model;

import com.agenda.task.model.TaskId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Event {
    private final EventId eventId;
    private String text;
    private LocalDate eventDate;
    private final LocalDateTime createdAt;
    private RecurrenceType recurrenceType;
    private LocalDate repeatUntil;
    private final List<TaskId> taskIds;

    public Event(String text, LocalDate eventDate) {
        checkInputText(text);
        checkInputEventDate(eventDate);

        this.text = text;
        this.eventDate = eventDate;

        eventId = null;
        createdAt = LocalDateTime.now();
        recurrenceType = RecurrenceType.NONE;
        repeatUntil = null;
        taskIds = new ArrayList<>();
    }

    public Event(EventId eventId, String text, LocalDate eventDate, LocalDateTime createdAt,
                 RecurrenceType recurrenceType, LocalDate repeatUntil, List<TaskId> taskIds) {
        checkInputEventId(eventId);
        checkInputText(text);
        checkInputEventDate(eventDate);
        checkInputCreatedAt(createdAt);
        checkInputRecurrenceType(recurrenceType);
        checkRecurrenceDates(recurrenceType, eventDate, repeatUntil);

        if (taskIds == null) {
            throw new IllegalArgumentException("TaskIds must not be NULL");
        }

        this.eventId = eventId;
        this.text = text;
        this.eventDate = eventDate;
        this.createdAt = createdAt;
        this.recurrenceType = recurrenceType;
        this.repeatUntil = repeatUntil;
        this.taskIds = new ArrayList<>(taskIds);
    }

    public LocalDate getRepeatUntil() {
        return repeatUntil;
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public String getText() {
        return text;
    }

    public EventId getEventId() {
        return eventId;
    }

    public List<TaskId> getTaskIds() {
        return List.copyOf(taskIds);
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

    private static void checkInputEventDate(LocalDate eventDate) {
        if (eventDate == null) {
            throw new IllegalArgumentException("Event date must not be NULL");
        }
    }

    private static void checkInputEventId(EventId eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("EventId must not be NULL");
        }
    }

    private static void checkInputCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt must not be NULL");
        }
    }

    private static void checkInputRecurrenceType(RecurrenceType recurrenceType) {
        if (recurrenceType == null) {
            throw new IllegalArgumentException("RecurrenceType must not be NULL");
        }
    }

    private static void checkRecurrenceDates(RecurrenceType recurrenceType, LocalDate eventDate, LocalDate repeatUntil) {
        if (recurrenceType == RecurrenceType.NONE && repeatUntil != null) {
            throw new IllegalArgumentException("Repeat until must be NULL when recurrence type is NONE");
        }

        if (recurrenceType != RecurrenceType.NONE && repeatUntil != null && repeatUntil.isBefore(eventDate)) {
            throw new IllegalArgumentException("Repeat until date must not be before event date");
        }
    }

    public void updateDetails(String text, LocalDate eventDate) {
        checkInputText(text);
        checkInputEventDate(eventDate);
        checkRecurrenceDates(recurrenceType, eventDate, repeatUntil);

        this.text = text;
        this.eventDate = eventDate;
    }

    public void addTask(TaskId taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("TaskId must not be NULL");
        }

        if (!taskIds.contains(taskId)) {
            taskIds.add(taskId);
        }
    }

    public void configureRecurrence(RecurrenceType recurrenceType, LocalDate repeatUntil) {
        checkInputRecurrenceType(recurrenceType);
        checkRecurrenceDates(recurrenceType, eventDate, repeatUntil);

        if (recurrenceType == RecurrenceType.NONE) {
            this.recurrenceType = RecurrenceType.NONE;
            this.repeatUntil = null;
            return;
        }

        this.recurrenceType = recurrenceType;
        this.repeatUntil = repeatUntil;
    }

    public void removeTask(TaskId taskId) {
        taskIds.remove(taskId);
    }

}

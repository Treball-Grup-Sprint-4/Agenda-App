package com.agenda.task.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private TaskId id;
    private String text;
    TaskPriority priority;
    TaskStatus status;
    LocalDateTime createdAt;
    LocalDate expirationDate;
    LocalDateTime completedAt;
}

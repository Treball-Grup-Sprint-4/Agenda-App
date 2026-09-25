package com.agenda.task.dto;

import com.agenda.task.model.TaskId;
import com.agenda.task.model.TaskPriority;
import com.agenda.task.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskDto(
        TaskId id,
        String text,
        TaskPriority priority,
        TaskStatus status,
        LocalDate expirationDate,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) { }

package com.agenda.event.dto;

import com.agenda.event.model.EventId;
import com.agenda.event.model.RecurrenceType;
import com.agenda.task.model.TaskId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record EventDto(EventId eventId, String text, LocalDate eventDate, LocalDateTime createdAt,
        RecurrenceType recurrenceType, LocalDate repeatUntil, List<TaskId> taskIds) {
}

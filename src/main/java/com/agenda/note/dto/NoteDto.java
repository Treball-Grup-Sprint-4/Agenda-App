package com.agenda.note.dto;

import com.agenda.note.model.NoteId;
import com.agenda.task.model.TaskId;

import java.time.LocalDateTime;

public record NoteDto(NoteId noteId, String content, LocalDateTime createdAt, TaskId taskId) {

}

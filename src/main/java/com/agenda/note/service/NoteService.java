package com.agenda.note.service;

import com.agenda.common.exception.TaskNotFoundException;
import com.agenda.note.dto.NoteDto;
import com.agenda.note.model.Note;
import com.agenda.note.repository.NoteRepository;
import com.agenda.task.repository.TaskRepository;
import com.agenda.common.exception.NoteNotFoundException;
import com.agenda.note.model.NoteId;
import com.agenda.task.model.TaskId;

import java.util.List;

public class NoteService {
    private final NoteRepository noteRepository;
    private final TaskRepository taskRepository;

    public NoteService(NoteRepository noteRepository, TaskRepository taskRepository) {
        if (noteRepository == null) {
            throw new IllegalArgumentException("NoteRepository must not be NULL");
        }

        if (taskRepository == null) {
            throw new IllegalArgumentException("TaskRepository must not be NULL");
        }

        this.noteRepository = noteRepository;
        this.taskRepository = taskRepository;
    }

    private static void checkNoteDto(NoteDto noteDto) {
        if (noteDto == null) {
            throw new IllegalArgumentException("Note DTO must not be NULL");
        }
    }

    private static void checkNoteId(NoteId noteId) {
        if (noteId == null) {
            throw new IllegalArgumentException("Note ID must not be NULL");
        }
    }

    private static void checkTaskId(TaskId taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("Task ID must not be NULL");
        }
    }

    private Note findNoteOrThrow(NoteId noteId) {
        return noteRepository.findById(noteId).orElseThrow(() ->
                new NoteNotFoundException("Note with ID " + noteId.value() + " not found"));
    }

    private void checkTaskExists(TaskId taskId) {
        taskRepository.findById(taskId).orElseThrow(() ->
                new TaskNotFoundException("Task with ID " + taskId.value() + " not found"));
    }

    public NoteDto create(NoteDto noteDto) {

        checkNoteDto(noteDto);

        checkTaskId(noteDto.taskId());

        checkTaskExists(noteDto.taskId());

        Note note = new Note(noteDto.content(), noteDto.taskId());

        Note savedNote = noteRepository.save(note);

        return toDto(savedNote);
    }

    private NoteDto toDto(Note note) {
        return new NoteDto(note.getNoteId(), note.getContent(), note.getCreatedAt(), note.getTaskId());
    }

    public NoteDto update(NoteId noteId, NoteDto noteDto) {

        checkNoteDto(noteDto);

        checkNoteId(noteId);

        checkTaskId(noteDto.taskId());

        Note note = findNoteOrThrow(noteId);

        checkTaskExists(noteDto.taskId());

        note.updateContent(noteDto.content());
        note.changeTask(noteDto.taskId());

        Note updatedNote = noteRepository.save(note);

        return toDto(updatedNote);
    }

    public void delete(NoteId noteId) {

        checkNoteId(noteId);

        findNoteOrThrow(noteId);

        noteRepository.deleteById(noteId);
    }

    public List<NoteDto> findAll() {
        return noteRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<NoteDto> findByTaskId(TaskId taskId) {

        checkTaskId(taskId);

        checkTaskExists(taskId);

        return noteRepository.findByTaskId(taskId).stream().map(this::toDto).toList();
    }

    public NoteDto findById(NoteId noteId) {

        checkNoteId(noteId);

        Note note = findNoteOrThrow(noteId);

        return toDto(note);
    }

}


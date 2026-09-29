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

    public NoteDto create(NoteDto noteDto) {

        taskRepository.findById(noteDto.taskId()).orElseThrow(() ->
                new TaskNotFoundException("Task with ID " + noteDto.taskId().value() + " not found"));

        Note note = new Note(noteDto.content(), noteDto.taskId());

        Note savedNote = noteRepository.save(note);

        return toDto(savedNote);
    }

    private NoteDto toDto(Note note) {
        return new NoteDto(note.getNoteId(), note.getContent(), note.getCreatedAt(), note.getTaskId());
    }

    public NoteDto update(NoteId noteId, NoteDto noteDto) {

        Note note = noteRepository.findById(noteId).orElseThrow(() -> new NoteNotFoundException("Note with ID " +
                noteId.value() + " not found"));

        taskRepository.findById(noteDto.taskId()).orElseThrow(() ->
                        new TaskNotFoundException("Task with ID " + noteDto.taskId().value() + " not found"));

        note.updateContent(noteDto.content());
        note.changeTask(noteDto.taskId());

        Note updatedNote = noteRepository.save(note);

        return toDto(updatedNote);
    }

    public void delete(NoteId noteId) {

        noteRepository.findById(noteId).orElseThrow(() -> new NoteNotFoundException("Note with ID " + noteId.value() +
                " not found"));

        noteRepository.deleteById(noteId);
    }

    public List<NoteDto> findAll() {
        return noteRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<NoteDto> findByTaskId(TaskId taskId) {

        taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task with ID " + taskId.value() +
                " not found"));

        return noteRepository.findByTaskId(taskId).stream().map(this::toDto).toList();
    }

    public NoteDto findById(NoteId noteId) {
        Note note = noteRepository.findById(noteId).orElseThrow(() ->
                new NoteNotFoundException("Note with ID " + noteId.value() + " not found"));

        return toDto(note);
    }

}

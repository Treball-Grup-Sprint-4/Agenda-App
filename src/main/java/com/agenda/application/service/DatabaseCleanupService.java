package com.agenda.application.service;

import com.agenda.common.exception.EmptyDatabaseException;
import com.agenda.event.model.Event;
import com.agenda.event.repository.EventRepository;
import com.agenda.note.model.Note;
import com.agenda.note.repository.NoteRepository;
import com.agenda.task.model.Task;
import com.agenda.task.repository.TaskRepository;

import java.util.List;

public class DatabaseCleanupService {

    private final NoteRepository noteRepository;
    private final TaskRepository taskRepository;
    private final EventRepository eventRepository;

    public DatabaseCleanupService(NoteRepository noteRepository, TaskRepository taskRepository,
                                  EventRepository eventRepository) {

        this.noteRepository = noteRepository;
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
    }

    public void deleteAll() {
        List<Note> notes = noteRepository.findAll();
        List<Task> tasks = taskRepository.findAll();
        List<Event> events = eventRepository.findAll();

        if (notes.isEmpty() && tasks.isEmpty() && events.isEmpty()) {
            throw new EmptyDatabaseException("Database is already empty.");
        }

        notes.forEach(note -> noteRepository.deleteById(note.getNoteId()));

        tasks.forEach(task -> taskRepository.deleteById(task.getId()));

        events.forEach(event -> eventRepository.deleteById(event.getEventId()));
    }
}

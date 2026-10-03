package com.agenda.application.service;

import com.agenda.common.exception.EmptyDatabaseException;
import com.agenda.event.model.Event;
import com.agenda.event.model.EventId;
import com.agenda.event.model.RecurrenceType;
import com.agenda.event.repository.EventRepository;
import com.agenda.note.model.Note;
import com.agenda.note.model.NoteId;
import com.agenda.note.repository.NoteRepository;
import com.agenda.task.model.*;
import com.agenda.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseCleanupServiceTest {

    private FakeNoteRepository noteRepository;
    private FakeTaskRepository taskRepository;
    private FakeEventRepository eventRepository;
    private DatabaseCleanupService databaseCleanupService;

    @BeforeEach
    void setUp() {
        noteRepository = new FakeNoteRepository();
        taskRepository = new FakeTaskRepository();
        eventRepository = new FakeEventRepository();

        databaseCleanupService = new DatabaseCleanupService(noteRepository, taskRepository, eventRepository);
    }

    @Test
    void shouldDeleteAllDatabaseContent() {
        TaskId taskId = new TaskId(1);

        Note note = new Note(new NoteId(1), "Note", LocalDateTime.now(), taskId);

        Task task = new Task(taskId, "Task", TaskPriority.MEDIUM, TaskStatus.PENDING, null,
                LocalDateTime.now(), null);

        Event event = new Event(new EventId(1), "Event", LocalDate.now(), LocalDateTime.now(),
                RecurrenceType.NONE, null, List.of());

        noteRepository.notes.add(note);
        taskRepository.tasks.add(task);
        eventRepository.events.add(event);

        databaseCleanupService.deleteAll();

        assertTrue(noteRepository.findAll().isEmpty());
        assertTrue(taskRepository.findAll().isEmpty());
        assertTrue(eventRepository.findAll().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenDatabaseIsAlreadyEmpty() {
        assertThrows(EmptyDatabaseException.class, databaseCleanupService::deleteAll);
    }

    private static class FakeNoteRepository implements NoteRepository {

        private final List<Note> notes = new ArrayList<>();

        @Override
        public Note save(Note note) {
            notes.add(note);
            return note;
        }

        @Override
        public Optional<Note> findById(NoteId noteId) {
            return notes.stream().filter(note -> note.getNoteId().equals(noteId)).findFirst();
        }

        @Override
        public List<Note> findAll() {
            return new ArrayList<>(notes);
        }

        @Override
        public List<Note> findByTaskId(TaskId taskId) {
            return notes.stream().filter(note -> note.getTaskId().equals(taskId)).toList();
        }

        @Override
        public void deleteById(NoteId noteId) {
            notes.removeIf(note -> note.getNoteId().equals(noteId));
        }
    }

    private static class FakeTaskRepository implements TaskRepository {

        private final List<Task> tasks = new ArrayList<>();

        @Override
        public Task save(Task task) {
            tasks.add(task);
            return task;
        }

        @Override
        public Optional<Task> findById(TaskId taskId) {
            return tasks.stream().filter(task -> task.getId().equals(taskId)).findFirst();
        }

        @Override
        public List<Task> findAll() {
            return new ArrayList<>(tasks);
        }

        @Override
        public void deleteById(TaskId taskId) {
            tasks.removeIf(task -> task.getId().equals(taskId));
        }
    }

    private static class FakeEventRepository implements EventRepository {

        private final List<Event> events = new ArrayList<>();

        @Override
        public Event save(Event event) {
            events.add(event);
            return event;
        }

        @Override
        public Optional<Event> findById(EventId eventId) {
            return events.stream().filter(event -> event.getEventId().equals(eventId)).findFirst();
        }

        @Override
        public List<Event> findAll() {
            return new ArrayList<>(events);
        }

        @Override
        public void deleteById(EventId eventId) {
            events.removeIf(event -> event.getEventId().equals(eventId));
        }
    }
}


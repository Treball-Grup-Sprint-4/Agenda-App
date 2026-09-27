package com.agenda.note.service;

import com.agenda.common.exception.NoteNotFoundException;
import com.agenda.common.exception.TaskNotFoundException;
import com.agenda.note.dto.NoteDto;
import com.agenda.note.model.Note;
import com.agenda.note.model.NoteId;
import com.agenda.note.repository.NoteRepository;
import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;
import com.agenda.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NoteServiceTest {
    private FakeNoteRepository noteRepository;
    private FakeTaskRepository taskRepository;
    private NoteService noteService;

    @BeforeEach
    void setUp() {
        noteRepository = new FakeNoteRepository();
        taskRepository = new FakeTaskRepository();
        noteService = new NoteService(noteRepository, taskRepository);
    }

    private static class FakeNoteRepository implements NoteRepository {
        private final List<Note> notes = new ArrayList<>();
        private int nextId = 1;

        @Override
        public Note save(Note note) {
            if (note.getNoteId() == null) {
                Note savedNote = new Note(new NoteId(nextId++), note.getContent(), note.getCreatedAt(),
                        note.getTaskId());

                notes.add(savedNote);
                return savedNote;
            }

            notes.removeIf(existing -> existing.getNoteId().equals(note.getNoteId()));

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

        private final List<TaskId> existingTaskIds = new ArrayList<>();

        void add(TaskId taskId) {
            existingTaskIds.add(taskId);
        }

        @Override
        public Optional<Task> findById(TaskId taskId) {
            if (existingTaskIds.contains(taskId)) {
                return Optional.of(new Task("Exist", null));
            }

            return Optional.empty();
        }

        @Override
        public Task save(Task task) {
            return task;
        }

        @Override
        public List<Task> findAll() {
            return List.of();
        }

        @Override
        public void deleteById(TaskId taskId) {
        }
    }

    @Test
    void shouldCreateNoteWhenTaskExists() {
        TaskId taskId = new TaskId(1);
        taskRepository.add(taskId);

        NoteDto noteDto = new NoteDto(null, "Note", null, taskId);

        NoteDto createdNote = noteService.create(noteDto);

        assertNotNull(createdNote.noteId());
        assertEquals("Note", createdNote.content());
        assertEquals(taskId, createdNote.taskId());
        assertNotNull(createdNote.createdAt());
    }

    @Test
    void shouldThrowExceptionWhenCreatingNoteWithNonExistingTask() {
        TaskId taskId = new TaskId(1);

        NoteDto noteDto = new NoteDto(null, "Test note", null, taskId);

        assertThrows(TaskNotFoundException.class, () -> noteService.create(noteDto));
    }

    @Test
    void shouldUpdateNoteWhenNoteAndTaskExist() {
        TaskId originalTaskId = new TaskId(1);
        TaskId newTaskId = new TaskId(2);

        taskRepository.add(originalTaskId);
        taskRepository.add(newTaskId);

        NoteDto createdNote = noteService.create(new NoteDto(null, "Original", null,
                originalTaskId));

        NoteDto updatedNote = noteService.update(createdNote.noteId(), new NoteDto(createdNote.noteId(),
                "Updated", createdNote.createdAt(), newTaskId));

        assertEquals("Updated", updatedNote.content());
        assertEquals(newTaskId, updatedNote.taskId());
        assertEquals(createdNote.noteId(), updatedNote.noteId());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingNote() {
        TaskId taskId = new TaskId(1);
        taskRepository.add(taskId);

        NoteDto noteDto = new NoteDto(new NoteId(99), "Note", null, taskId);

        assertThrows(NoteNotFoundException.class, () -> noteService.update(new NoteId(99), noteDto));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithNonExistingTask() {
        TaskId originalTaskId = new TaskId(1);
        TaskId newTaskId = new TaskId(2);

        taskRepository.add(originalTaskId);

        NoteDto createdNote = noteService.create(new NoteDto(null, "Original", null,
                originalTaskId));

        NoteDto updatedNote = new NoteDto(createdNote.noteId(), "Updated", createdNote.createdAt(),
                newTaskId);

        assertThrows(TaskNotFoundException.class, () -> noteService.update(createdNote.noteId(), updatedNote));
    }

    @Test
    void shouldDeleteExistingNote() {
        TaskId taskId = new TaskId(1);
        taskRepository.add(taskId);

        NoteDto createdNote = noteService.create(new NoteDto(null, "Note", null,
                taskId));

        noteService.delete(createdNote.noteId());

        assertTrue(noteRepository.findById(createdNote.noteId()).isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingNote() {
        NoteId noteId = new NoteId(6);

        assertThrows(NoteNotFoundException.class, () -> noteService.delete(noteId));
    }

    @Test
    void shouldFindAllNotes() {
        TaskId taskId = new TaskId(1);
        taskRepository.add(taskId);

        noteService.create(new NoteDto(null, "Note 1", null, taskId));

        noteService.create(new NoteDto(null, "Note 2", null, taskId));

        List<NoteDto> notes = noteService.findAll();

        assertEquals(2, notes.size());
    }

    @Test
    void shouldFindNotesByTaskId() {
        TaskId firstTaskId = new TaskId(1);
        TaskId secondTaskId = new TaskId(2);

        taskRepository.add(firstTaskId);
        taskRepository.add(secondTaskId);

        noteService.create(new NoteDto(null, "Task Note 1", null, firstTaskId));

        noteService.create(new NoteDto(null, "Task Note 2", null, secondTaskId));

        List<NoteDto> notes = noteService.findByTaskId(firstTaskId);

        assertEquals(1, notes.size());
        assertEquals("Task Note 1", notes.getFirst().content());
        assertEquals(firstTaskId, notes.getFirst().taskId());
    }

    @Test
    void shouldThrowExceptionWhenFindingNotesByNonExistingTask() {
        TaskId taskId = new TaskId(8);

        assertThrows(TaskNotFoundException.class, () -> noteService.findByTaskId(taskId));
    }

    @Test
    void shouldFindNoteById() {
        TaskId taskId = new TaskId(1);
        taskRepository.add(taskId);

        NoteDto createdNote = noteService.create(new NoteDto(null, "Note", null, taskId));

        NoteDto foundNote = noteService.findById(createdNote.noteId());

        assertEquals(createdNote.noteId(), foundNote.noteId());
        assertEquals("Note", foundNote.content());
        assertEquals(taskId, foundNote.taskId());
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistingNoteById() {
        NoteId noteId = new NoteId(44);

        assertThrows(NoteNotFoundException.class, () -> noteService.findById(noteId));
    }
}


package com.agenda.note.model;

import com.agenda.task.model.TaskId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NoteTest {

    @Test
    void shouldCreateValidNote() {
        TaskId taskId = new TaskId(1);

        Note note = new Note("Test", taskId);

        assertEquals("Test", note.getContent());
        assertEquals(taskId, note.getTaskId());
        assertNotNull(note.getCreatedAt());
        assertNull(note.getNoteId());
    }

    @Test
    void shouldUpdateContent() {
        Note note = new Note("Original", new TaskId(1));

        note.updateContent("Updated");

        assertEquals("Updated", note.getContent());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithInvalidContent() {
        Note note = new Note("Original", new TaskId(1));

        assertThrows(IllegalArgumentException.class, () -> note.updateContent("  "));
    }

    @Test
    void shouldChangeTask() {
        Note note = new Note("Note", new TaskId(1));
        TaskId newTaskId = new TaskId(2);

        note.changeTask(newTaskId);

        assertEquals(newTaskId, note.getTaskId());
    }

    @Test
    void shouldThrowExceptionWhenChangingTaskToNull() {
        Note note = new Note("Note", new TaskId(1));

        assertThrows(IllegalArgumentException.class, () -> note.changeTask(null));
    }

    @Test
    void shouldThrowExceptionWhenTaskIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Note("Note", null));
    }

    @Test
    void shouldThrowExceptionWhenContentIsNull() {
        TaskId taskId = new TaskId(1);

        assertThrows(IllegalArgumentException.class, () -> new Note(null, taskId));
    }

    @Test
    void shouldThrowExceptionWhenReconstructedNoteIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Note(null, "Test note",
                LocalDateTime.now(), new TaskId(1)));
    }

    @Test
    void shouldThrowExceptionWhenReconstructedCreatedAtIsNull() {

        assertThrows(IllegalArgumentException.class, () -> new Note(new NoteId(1), "Test note",
                null, new TaskId(1)));
    }

    @Test
    void shouldThrowExceptionWhenContentIsBlank() {
        TaskId taskId = new TaskId(1);

        assertThrows(IllegalArgumentException.class, () -> new Note("  ", taskId));
    }

    @Test
    void shouldThrowExceptionWhenContentExceeds800Characters() {
        TaskId taskId = new TaskId(1);
        String content = "a".repeat(801);

        assertThrows(IllegalArgumentException.class, () -> new Note(content, taskId));
    }

}

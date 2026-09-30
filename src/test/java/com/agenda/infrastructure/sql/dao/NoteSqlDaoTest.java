package com.agenda.infrastructure.sql.dao;

import com.agenda.common.exception.PersistenceException;
import com.agenda.common.persistence.DatabaseConnection;
import com.agenda.note.model.Note;
import com.agenda.note.model.NoteId;
import com.agenda.task.model.TaskId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoteSqlDaoTest {
    private NoteSqlDao noteSqlDao;
    private TaskId taskId;

    @BeforeEach
    void setUp() throws SQLException {
        noteSqlDao = new NoteSqlDao();

        String sql = """
                INSERT INTO task (text, priority, status, created_at)
                VALUES (?, 'MEDIUM', 'PENDING', CURRENT_TIMESTAMP)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, "NoteSqlDaoTest");
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    taskId = new TaskId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM note WHERE task_id = ?")) {
                statement.setInt(1, taskId.value());
                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM task WHERE id = ?")) {
                statement.setInt(1, taskId.value());
                statement.executeUpdate();
            }
        }
    }

    @Test
    void shouldSaveNewNote() {
        Note note = new Note("Test", taskId);

        Note savedNote = noteSqlDao.save(note);

        assertNotNull(savedNote.getNoteId());
        assertEquals("Test", savedNote.getContent());
        assertEquals(taskId, savedNote.getTaskId());
    }

    @Test
    void shouldThrowExceptionWhenSavingNullNote() {
        assertThrows(IllegalArgumentException.class, () -> noteSqlDao.save(null));
    }

    @Test
    void shouldFindNoteById() {
        Note savedNote = noteSqlDao.save(new Note("Test", taskId));

        Note foundNote = noteSqlDao.findById(savedNote.getNoteId()).orElseThrow();

        assertEquals(savedNote.getNoteId(), foundNote.getNoteId());
        assertEquals("Test", foundNote.getContent());
        assertEquals(taskId, foundNote.getTaskId());
    }

    @Test
    void shouldUpdateNote() {
        Note savedNote = noteSqlDao.save(new Note("Original", taskId));

        savedNote.updateContent("Updated");
        noteSqlDao.save(savedNote);

        Note updatedNote = noteSqlDao.findById(savedNote.getNoteId()).orElseThrow();

        assertEquals("Updated", updatedNote.getContent());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingNote() {
        Note note = new Note(new NoteId(87), "Updated", LocalDateTime.now(), taskId);

        assertThrows(PersistenceException.class, () -> noteSqlDao.save(note));
    }

    @Test
    void shouldDeleteNote() {
        Note savedNote = noteSqlDao.save(new Note("Note for delete", taskId));

        noteSqlDao.deleteById(savedNote.getNoteId());

        assertTrue(noteSqlDao.findById(savedNote.getNoteId()).isEmpty());
    }

    @Test
    void shouldFindAllNotes() {
        Note firstNote = noteSqlDao.save(new Note("First", taskId));
        Note secondNote = noteSqlDao.save(new Note("Second", taskId));

        List<Note> notes = noteSqlDao.findAll();

        assertTrue(notes.stream().anyMatch(note -> note.getNoteId().equals(firstNote.getNoteId())));

        assertTrue(notes.stream().anyMatch(note -> note.getNoteId().equals(secondNote.getNoteId())));
    }

    @Test
    void shouldFindNotesByTaskId() {


        Note firstNote = noteSqlDao.save(new Note("First", taskId));
        Note secondNote = noteSqlDao.save(new Note("Second", taskId));

        List<Note> notes = noteSqlDao.findByTaskId(taskId);

        assertTrue(notes.stream().anyMatch(note -> note.getNoteId().equals(firstNote.getNoteId())));

        assertTrue(notes.stream().anyMatch(note -> note.getNoteId().equals(secondNote.getNoteId())));
    }

    @Test
    void shouldThrowExceptionWhenFindingByNullNoteId() {
        assertThrows(IllegalArgumentException.class, () -> noteSqlDao.findById(null));
    }

    @Test
    void shouldThrowExceptionWhenDeletingByNullNoteId() {
        assertThrows(IllegalArgumentException.class, () -> noteSqlDao.deleteById(null));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingNote() {
        assertThrows(PersistenceException.class, () -> noteSqlDao.deleteById(new NoteId(34)));
    }
}

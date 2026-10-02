package com.agenda.infrastructure.sql.dao;

import com.agenda.common.exception.PersistenceException;
import com.agenda.note.model.Note;
import com.agenda.note.model.NoteId;
import com.agenda.note.repository.NoteRepository;
import com.agenda.task.model.TaskId;
import com.agenda.common.persistence.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NoteSqlDao implements NoteRepository {

    private Note insert(Note note) {
        String sql = """
            INSERT INTO note (content, created_at, task_id)
            VALUES (?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, note.getContent());
            statement.setTimestamp(2, Timestamp.valueOf(note.getCreatedAt()));
            statement.setInt(3, note.getTaskId().value());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    NoteId noteId = new NoteId(generatedKeys.getInt(1));

                    return new Note(noteId, note.getContent(), note.getCreatedAt(), note.getTaskId());
                }
            }

            throw new PersistenceException("Failed to retrieve generated Note ID");

        } catch (SQLException e) {
            throw new PersistenceException("Failed to save note", e);
        }
    }

    private Note update(Note note) {
        String sql = """
            UPDATE note
            SET content = ?, task_id = ?
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, note.getContent());
            statement.setInt(2, note.getTaskId().value());
            statement.setInt(3, note.getNoteId().value());

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new PersistenceException("Failed to update note: Note ID not found");
            }

            return note;

        } catch (SQLException e) {
            throw new PersistenceException("Failed to update note", e);
        }
    }

    @Override
    public Note save(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Note must not be NULL");
        }

        if (note.getNoteId() == null) {
            return insert(note);
        }

        return update(note);
    }

    private Note mapNote(ResultSet resultSet) throws SQLException {
        return new Note(new NoteId(resultSet.getInt("id")), resultSet.getString("content"),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                new TaskId(resultSet.getInt("task_id")));
    }

    @Override
    public Optional<Note> findById(NoteId noteId) {
        if (noteId == null) {
            throw new IllegalArgumentException("NoteId must not be NULL");
        }

        String sql = """
        SELECT id, content, created_at, task_id
        FROM note
        WHERE id = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, noteId.value());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapNote(resultSet));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new PersistenceException("Failed to find note by ID", e);
        }
    }

    @Override
    public List<Note> findAll() {
        String sql = """
            SELECT id, content, created_at, task_id
            FROM note
            """;

        List<Note> notes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                notes.add(mapNote(resultSet));
            }

            return notes;

        } catch (SQLException e) {
            throw new PersistenceException("Failed to find all notes", e);
        }
    }

    @Override
    public List<Note> findByTaskId(TaskId taskId) {

        if (taskId == null) {
            throw new IllegalArgumentException("Task ID must not be NULL");
        }

        String sql = """
            SELECT id, content, created_at, task_id
            FROM note
            WHERE task_id = ?
            """;

        List<Note> notes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, taskId.value());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    notes.add(mapNote(resultSet));
                }
            }

            return notes;

        } catch (SQLException e) {
            throw new PersistenceException("Failed to find notes by Task ID", e);
        }
    }

    @Override
    public void deleteById(NoteId noteId) {
        if (noteId == null) {
            throw new IllegalArgumentException("NoteId must not be NULL");
        }

        String sql = """
        DELETE FROM note
        WHERE id = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, noteId.value());

            int deletedRows = statement.executeUpdate();

            if (deletedRows == 0) {
                throw new PersistenceException("Failed to delete note: Note ID not found");
            }

        } catch (SQLException e) {
            throw new PersistenceException("Failed to delete note", e);
        }
    }
}

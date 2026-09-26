package com.agenda.note.repository;

import com.agenda.note.model.Note;
import com.agenda.note.model.NoteId;
import com.agenda.task.model.TaskId;

import java.util.List;
import java.util.Optional;

public interface NoteRepository {

    Note save(Note note);

    Optional<Note> findById(NoteId noteId);

    List<Note> findAll();

    List<Note> findByTaskId(TaskId taskId);

    void deleteById(NoteId noteId);
}

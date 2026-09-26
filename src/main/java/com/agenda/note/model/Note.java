package com.agenda.note.model;

import com.agenda.task.model.TaskId;

import java.time.LocalDateTime;

public class Note {
    private NoteId noteId;
    private String content;
    private final LocalDateTime createdAt;
    private TaskId taskId;

    public Note(String content, TaskId taskId) {
        checkInputContent(content);
        checkInputTaskId(taskId);

        this.content = content;
        this.taskId = taskId;

        createdAt = LocalDateTime.now();
    }

    public Note(NoteId noteId, String content, LocalDateTime createdAt, TaskId taskId) {
        checkInputContent(content);
        checkInputTaskId(taskId);
        checkInputCreatedAt(createdAt);
        checkInputId(noteId);

        this.noteId = noteId;
        this.content = content;
        this.createdAt = createdAt;
        this.taskId = taskId;
    }

    public NoteId getNoteId() {
        return noteId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public TaskId getTaskId() {
        return taskId;
    }



    private static void checkInputContent(String content) {
        if(content == null) {
            throw new IllegalArgumentException("Content must not be NULL");
        }

        if(content.isBlank()) {
            throw new IllegalArgumentException("Content must not be empty");
        }

        if (content.length() > 800) {
            throw new IllegalArgumentException("Content must not exceed 800 characters");
        }
    }

    private static void checkInputTaskId(TaskId taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("TaskId must not be NULL");
        }
    }

    private static void checkInputId(NoteId id) {
        if (id == null) {
            throw new IllegalArgumentException("NoteId must not be NULL");
        }
    }

    private static void checkInputCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt must not be NULL");
        }
    }

    public void updateContent(String content) {
        checkInputContent(content);

        this.content = content;
    }

    public void changeTask(TaskId taskId) {
        checkInputTaskId(taskId);

        this.taskId = taskId;
    }


}

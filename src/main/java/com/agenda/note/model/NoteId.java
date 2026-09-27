package com.agenda.note.model;

public record NoteId(int value) {

    public NoteId {
        if (value <= 0) {
            throw new IllegalArgumentException("Note ID must be greater than 0");
        }
    }
}

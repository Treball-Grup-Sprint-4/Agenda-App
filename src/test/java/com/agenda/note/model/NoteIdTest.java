package com.agenda.note.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoteIdTest {

    @Test
    void shouldCreateValidNoteId() {
        NoteId noteId = new NoteId(1);

        assertEquals(1, noteId.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsZero() {

        assertThrows(IllegalArgumentException.class, () -> new NoteId(0));
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {

        assertThrows(IllegalArgumentException.class, () -> new NoteId(-1));
    }
}

package com.agenda.event.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventIdTest {

    @Test
    void shouldCreateEventIdWhenValueIsPositive() {
        EventId eventId = new EventId(1);

        assertEquals(1, eventId.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsZero() {
        assertThrows(IllegalArgumentException.class, () -> new EventId(0));
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new EventId(-1));
    }
}
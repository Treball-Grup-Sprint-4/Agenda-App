package com.agenda.event.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class NoRecurrencePolicyTest {

    @Test
    void shouldReturnEventDateWhenEventIsOnOrAfterReferenceDate() {
        RecurrencePolicy policy = new NoRecurrencePolicy();

        LocalDate eventDate = LocalDate.of(2026, 11, 10);
        LocalDate fromDate = LocalDate.of(2026, 11, 1);

        LocalDate result = policy.nextDate(eventDate, fromDate, null);

        assertEquals(eventDate, result);
    }

    @Test
    void shouldReturnNullWhenEventIsBeforeReferenceDate() {
        RecurrencePolicy policy = new NoRecurrencePolicy();

        LocalDate eventDate = LocalDate.of(2026, 11, 1);
        LocalDate fromDate = LocalDate.of(2026, 11, 10);

        LocalDate result = policy.nextDate(eventDate, fromDate, null);

        assertNull(result);
    }
}

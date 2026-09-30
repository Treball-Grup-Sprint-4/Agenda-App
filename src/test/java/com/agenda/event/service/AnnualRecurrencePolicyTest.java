package com.agenda.event.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AnnualRecurrencePolicyTest {

    @Test
    void shouldReturnNextAnnualOccurrence() {
        RecurrencePolicy policy = new AnnualRecurrencePolicy();

        LocalDate eventDate = LocalDate.of(2024, 10, 10);
        LocalDate fromDate = LocalDate.of(2026, 9, 28);

        LocalDate result = policy.nextDate(eventDate, fromDate, null);

        assertEquals(LocalDate.of(2026, 10, 10), result);
    }

    @Test
    void shouldReturnNullWhenNextAnnualOccurrenceIsAfterRepeatUntil() {
        RecurrencePolicy policy = new AnnualRecurrencePolicy();

        LocalDate eventDate = LocalDate.of(2024, 5, 21);
        LocalDate fromDate = LocalDate.of(2026, 10, 4);
        LocalDate repeatUntil = LocalDate.of(2026, 10, 30);

        LocalDate result = policy.nextDate(eventDate, fromDate, repeatUntil);

        assertNull(result);
    }

}
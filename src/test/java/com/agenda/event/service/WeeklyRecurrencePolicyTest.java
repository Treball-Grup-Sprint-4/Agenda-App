package com.agenda.event.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WeeklyRecurrencePolicyTest {

    @Test
    void shouldReturnNextWeeklyOccurrence() {
        RecurrencePolicy policy = new WeeklyRecurrencePolicy();

        LocalDate eventDate = LocalDate.of(2026, 9, 1);
        LocalDate fromDate = LocalDate.of(2026, 9, 28);

        LocalDate result = policy.nextDate(eventDate, fromDate, null);

        assertEquals(LocalDate.of(2026, 9, 29), result);
    }

    @Test
    void shouldReturnNullWhenNextWeeklyOccurrenceIsAfterRepeatUntil() {
        RecurrencePolicy policy = new WeeklyRecurrencePolicy();

        LocalDate eventDate = LocalDate.of(2026, 10, 2);
        LocalDate fromDate = LocalDate.of(2026, 11, 10);
        LocalDate repeatUntil = LocalDate.of(2026, 11, 10);

        LocalDate result = policy.nextDate(eventDate, fromDate, repeatUntil);

        assertNull(result);
    }

}
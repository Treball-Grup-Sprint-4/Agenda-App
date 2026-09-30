package com.agenda.event.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MonthlyRecurrencePolicyTest {

    @Test
    void shouldReturnNextMonthlyOccurrence() {
        RecurrencePolicy policy = new MonthlyRecurrencePolicy();

        LocalDate eventDate = LocalDate.of(2026, 6, 5);
        LocalDate fromDate = LocalDate.of(2026, 10, 15);

        LocalDate result = policy.nextDate(eventDate, fromDate, null);

        assertEquals(LocalDate.of(2026, 11, 5), result);
    }

    @Test
    void shouldReturnNullWhenNextMonthlyOccurrenceIsAfterRepeatUntil() {
        RecurrencePolicy policy = new MonthlyRecurrencePolicy();

        LocalDate eventDate = LocalDate.of(2026, 6, 10);
        LocalDate fromDate = LocalDate.of(2026, 9, 28);
        LocalDate repeatUntil = LocalDate.of(2026, 9, 30);

        LocalDate result = policy.nextDate(eventDate, fromDate, repeatUntil);

        assertNull(result);
    }

}

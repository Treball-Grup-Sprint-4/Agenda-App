package com.agenda.event.service;

import java.time.LocalDate;

public class MonthlyRecurrencePolicy implements RecurrencePolicy {

    @Override
    public LocalDate nextDate(LocalDate eventDate, LocalDate fromDate, LocalDate repeatUntil) {
        LocalDate nextDate = eventDate;

        while (nextDate.isBefore(fromDate)) {
            nextDate = nextDate.plusMonths(1);
        }

        if (repeatUntil != null && nextDate.isAfter(repeatUntil)) {
            return null;
        }

        return nextDate;
    }
}

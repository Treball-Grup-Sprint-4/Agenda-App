package com.agenda.event.service;

import java.time.LocalDate;

public class NoRecurrencePolicy implements RecurrencePolicy {

    @Override
    public LocalDate nextDate(LocalDate eventDate, LocalDate fromDate, LocalDate repeatUntil) {
        return eventDate.isBefore(fromDate) ? null : eventDate;
    }

}

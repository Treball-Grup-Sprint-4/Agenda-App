package com.agenda.event.service;

import java.time.LocalDate;

public interface RecurrencePolicy {

    LocalDate nextDate(LocalDate eventDate, LocalDate fromDate, LocalDate repeatUntil);
}

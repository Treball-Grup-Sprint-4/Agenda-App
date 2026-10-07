package com.agenda.event.service;

import com.agenda.event.model.RecurrenceType;

public class RecurrenceFactory {

    public RecurrencePolicy create(RecurrenceType type) {
        if (type == null) {
            throw new IllegalArgumentException("RecurrenceType must not be NULL");
        }

        return switch (type) {
            case NONE -> new NoRecurrencePolicy();
            case ANNUAL -> new AnnualRecurrencePolicy();
            case WEEKLY -> new WeeklyRecurrencePolicy();
            case MONTHLY -> new MonthlyRecurrencePolicy();
        };
    }
}

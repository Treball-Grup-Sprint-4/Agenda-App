package com.agenda.event.service;

import com.agenda.event.model.RecurrenceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecurrenceFactoryTest {

    @Test
    void shouldCreateNoRecurrencePolicy() {
        RecurrenceFactory factory = new RecurrenceFactory();

        RecurrencePolicy policy = factory.create(RecurrenceType.NONE);

        assertInstanceOf(NoRecurrencePolicy.class, policy);
    }

    @Test
    void shouldCreateAnnualRecurrencePolicy() {
        RecurrenceFactory factory = new RecurrenceFactory();

        RecurrencePolicy policy = factory.create(RecurrenceType.ANNUAL);

        assertInstanceOf(AnnualRecurrencePolicy.class, policy);
    }

    @Test
    void shouldCreateWeeklyRecurrencePolicy() {
        RecurrenceFactory factory = new RecurrenceFactory();

        RecurrencePolicy policy = factory.create(RecurrenceType.WEEKLY);

        assertInstanceOf(WeeklyRecurrencePolicy.class, policy);
    }

    @Test
    void shouldCreateMonthlyRecurrencePolicy() {
        RecurrenceFactory factory = new RecurrenceFactory();

        RecurrencePolicy policy = factory.create(RecurrenceType.MONTHLY);

        assertInstanceOf(MonthlyRecurrencePolicy.class, policy);
    }

    @Test
    void shouldThrowExceptionWhenRecurrenceTypeIsNull() {
        RecurrenceFactory factory = new RecurrenceFactory();

        assertThrows(IllegalArgumentException.class, () -> factory.create(null));
    }
}
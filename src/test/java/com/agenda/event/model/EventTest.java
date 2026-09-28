package com.agenda.event.model;

import com.agenda.task.model.TaskId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void shouldCreateValidEvent() {
        LocalDate eventDate = LocalDate.of(2026, 11, 10);

        Event event = new Event("Event", eventDate);

        assertEquals("Event", event.getText());
        assertEquals(eventDate, event.getEventDate());
        assertNotNull(event.getCreatedAt());
        assertNull(event.getEventId());
        assertEquals(RecurrenceType.NONE, event.getRecurrenceType());
        assertNull(event.getRepeatUntil());
        assertTrue(event.getTaskIds().isEmpty());
    }

    @Test
    void shouldCreateEventWithPastDate() {
        LocalDate pastDate = LocalDate.of(2020, 1, 1);

        Event event = new Event("Past event", pastDate);

        assertEquals(pastDate, event.getEventDate());
    }

    @Test
    void shouldThrowExceptionWhenTextIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Event(null, LocalDate.now()));
    }

    @Test
    void shouldThrowExceptionWhenTextIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Event(" ", LocalDate.now()));
    }

    @Test
    void shouldThrowExceptionWhenTextExceeds256Characters() {
        String text = "a".repeat(257);

        assertThrows(IllegalArgumentException.class, () -> new Event(text, LocalDate.now()));
    }

    @Test
    void shouldCreateEventWhenTextHasExactly256Characters() {
        String text = "u".repeat(256);

        Event event = new Event(text, LocalDate.now());

        assertEquals(text, event.getText());
    }

    @Test
    void shouldThrowExceptionWhenEventDateIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Event("Event", null));
    }

    @Test
    void shouldReconstructValidEvent() {
        EventId eventId = new EventId(1);
        TaskId taskId = new TaskId(1);
        LocalDate eventDate = LocalDate.of(2026, 10, 10);
        LocalDateTime createdAt = LocalDateTime.of(2026, 9, 27, 12, 0);

        Event event = new Event(eventId, "Event", eventDate, createdAt, RecurrenceType.MONTHLY,
                LocalDate.of(2027, 1, 10), List.of(taskId));

        assertEquals(eventId, event.getEventId());
        assertEquals("Event", event.getText());
        assertEquals(eventDate, event.getEventDate());
        assertEquals(createdAt, event.getCreatedAt());
        assertEquals(RecurrenceType.MONTHLY, event.getRecurrenceType());
        assertEquals(LocalDate.of(2027, 1, 10), event.getRepeatUntil());
        assertEquals(List.of(taskId), event.getTaskIds());
    }

    @Test
    void shouldThrowExceptionWhenReconstructedTaskIdsAreNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Event(new EventId(1), "Event", LocalDate.now(), LocalDateTime.now(), RecurrenceType.NONE,
                        null, null));
    }

    @Test
    void shouldThrowExceptionWhenReconstructedEventIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Event(null, "Event", LocalDate.now(), LocalDateTime.now(), RecurrenceType.NONE,
                        null, List.of()));
    }

    @Test
    void shouldThrowExceptionWhenReconstructedCreatedAtIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Event(new EventId(1), "Event", LocalDate.now(), null, RecurrenceType.NONE,
                        null, List.of()));
    }

    @Test
    void shouldThrowExceptionWhenReconstructedRecurrenceTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Event(new EventId(1), "Event", LocalDate.now(), LocalDateTime.now(), null,
                        null, List.of()));
    }

    @Test
    void shouldUpdateDetails() {
        Event event = new Event("Original", LocalDate.of(2026, 10, 10));
        LocalDate newDate = LocalDate.of(2026, 11, 20);

        event.updateDetails("Updated", newDate);

        assertEquals("Updated", event.getText());
        assertEquals(newDate, event.getEventDate());
    }

    @Test
    void shouldAddTask() {
        Event event = new Event("Meeting", LocalDate.of(2026, 12, 15));

        TaskId taskId = new TaskId(1);

        event.addTask(taskId);

        assertEquals(List.of(taskId), event.getTaskIds());
    }

    @Test
    void shouldNotAddDuplicatedTask() {
        Event event = new Event("Meeting", LocalDate.of(2026, 10, 25));

        TaskId taskId = new TaskId(1);

        event.addTask(taskId);
        event.addTask(taskId);

        assertEquals(1, event.getTaskIds().size());
    }

    @Test
    void shouldThrowWhenAddingNullTask() {
        Event event = new Event("Meeting", LocalDate.of(2026, 10, 3));

        assertThrows(IllegalArgumentException.class, () -> event.addTask(null));
    }

    @Test
    void shouldConfigureRecurrence() {
        Event event = new Event("Event", LocalDate.of(2026, 10, 10));
        LocalDate repeatUntil = LocalDate.of(2026, 11, 1);

        event.configureRecurrence(RecurrenceType.ANNUAL, repeatUntil);

        assertEquals(RecurrenceType.ANNUAL, event.getRecurrenceType());
        assertEquals(repeatUntil, event.getRepeatUntil());
    }

    @Test
    void shouldAllowInfiniteRecurrence() {
        Event event = new Event("Event", LocalDate.of(2026, 10,7 ));

        event.configureRecurrence(RecurrenceType.WEEKLY, null);

        assertEquals(RecurrenceType.WEEKLY, event.getRecurrenceType());
        assertNull(event.getRepeatUntil());
    }

    @Test
    void shouldClearRepeatUntilWhenRecurrenceIsNone() {
        Event event = new Event("Event", LocalDate.of(2027, 1, 10));

        event.configureRecurrence(RecurrenceType.MONTHLY, LocalDate.of(2027, 1, 10));

        event.configureRecurrence(RecurrenceType.NONE, null);

        assertEquals(RecurrenceType.NONE, event.getRecurrenceType());
        assertNull(event.getRepeatUntil());
    }

    @Test
    void shouldThrowExceptionWhenRepeatUntilIsBeforeEventDate() {
        Event event = new Event("Event", LocalDate.of(2026, 10, 10));

        assertThrows(IllegalArgumentException.class, () ->
                event.configureRecurrence(RecurrenceType.MONTHLY, LocalDate.of(2026, 10, 9)));
    }

    @Test
    void shouldThrowExceptionWhenRecurrenceTypeIsNull() {
        Event event = new Event("Event", LocalDate.of(2026, 10, 10));

        assertThrows(IllegalArgumentException.class, () ->
                event.configureRecurrence(null, null));
    }

    @Test
    void shouldThrowExceptionWhenReconstructedRepeatUntilIsBeforeEventDate() {
        LocalDate eventDate = LocalDate.of(2026, 10, 10);

        assertThrows(IllegalArgumentException.class, () ->
                new Event(new EventId(1), "Event", eventDate, LocalDateTime.now(), RecurrenceType.MONTHLY,
                        LocalDate.of(2026, 10, 9), List.of()));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingEventDateAfterRepeatUntil() {
        Event event = new Event("Event", LocalDate.of(2026, 10, 10));

        event.configureRecurrence(RecurrenceType.MONTHLY, LocalDate.of(2026, 12, 10));

        assertThrows(IllegalArgumentException.class, () -> event.updateDetails("Updated event",
                LocalDate.of(2027, 1, 10)));
    }

    @Test
    void shouldThrowExceptionWhenReconstructedNoneRecurrenceHasRepeatUntil() {
        assertThrows(IllegalArgumentException.class, () ->
                new Event(new EventId(1), "Event", LocalDate.of(2026, 10, 10),
                        LocalDateTime.now(), RecurrenceType.NONE, LocalDate.of(2026, 12, 10),
                        List.of()));
    }
}

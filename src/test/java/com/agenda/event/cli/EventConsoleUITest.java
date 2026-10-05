package com.agenda.event.cli;

import com.agenda.event.model.Event;
import com.agenda.event.model.EventId;
import com.agenda.event.model.RecurrenceType;
import com.agenda.event.repository.EventRepository;
import com.agenda.event.service.EventService;
import com.agenda.event.service.RecurrenceFactory;
import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;
import com.agenda.task.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class EventConsoleUITest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldConfigureRecurrenceAsNoneWhenInputIsEmpty() {
        FakeEventRepository eventRepository = new FakeEventRepository();

        Event event = createEvent(new EventId(1), RecurrenceType.WEEKLY,
                LocalDate.now().plusMonths(1));

        eventRepository.events.add(event);

        Scanner scanner = new Scanner("1\n\n");

        EventConsoleUI consoleUI = createConsoleUI(eventRepository, scanner);

        consoleUI.configureRecurrence();

        Event updatedEvent = eventRepository.findById(new EventId(1)).orElseThrow();

        assertEquals(RecurrenceType.NONE, updatedEvent.getRecurrenceType());
        assertNull(updatedEvent.getRepeatUntil());
    }

    @Test
    void shouldCreateEventWithNoneRecurrenceWhenInputIsEmpty() {
        FakeEventRepository eventRepository = new FakeEventRepository();

        LocalDate eventDate = LocalDate.now().plusDays(1);

        Scanner scanner = new Scanner("""
                New event
                %s

                """.formatted(eventDate));

        EventConsoleUI consoleUI = createConsoleUI(eventRepository, scanner);

        consoleUI.createEvent();

        Event createdEvent = eventRepository.findAll().getFirst();

        assertEquals("New event", createdEvent.getText());
        assertEquals(eventDate, createdEvent.getEventDate());
        assertEquals(RecurrenceType.NONE, createdEvent.getRecurrenceType());
        assertNull(createdEvent.getRepeatUntil());

        assertTrue(output.toString().contains("Event created successfully."));
    }

    @Test
    void shouldCancelEventCreationWhenPastDateIsNotConfirmed() {
        FakeEventRepository eventRepository = new FakeEventRepository();

        LocalDate pastDate = LocalDate.now().minusDays(1);

        Scanner scanner = new Scanner("""
                Past event
                %s
                N
                """.formatted(pastDate));

        EventConsoleUI consoleUI = createConsoleUI(eventRepository, scanner);

        consoleUI.createEvent();

        assertTrue(eventRepository.findAll().isEmpty());
        assertTrue(output.toString().contains("Event creation cancelled."));
    }

    @Test
    void shouldListNoEventsMessageWhenThereAreNoEvents() {
        FakeEventRepository eventRepository = new FakeEventRepository();

        EventConsoleUI consoleUI = createConsoleUI(eventRepository, new Scanner(""));

        consoleUI.listEvents();

        assertTrue(output.toString().contains("No events found."));
    }

    @Test
    void shouldListEventsWhenEventsExist() {
        FakeEventRepository eventRepository = new FakeEventRepository();

        Event event = createEvent(new EventId(1), RecurrenceType.NONE, null);

        eventRepository.events.add(event);

        EventConsoleUI consoleUI = createConsoleUI(eventRepository, new Scanner(""));

        consoleUI.listEvents();

        String result = output.toString();

        assertTrue(result.contains("ID: 1"));
        assertTrue(result.contains("Text: Event"));
        assertTrue(result.contains("Recurrence: NONE"));
    }

    @Test
    void shouldCancelDeletionWhenUserDoesNotConfirm() {
        FakeEventRepository eventRepository = new FakeEventRepository();

        Event event = createEvent(new EventId(1), RecurrenceType.NONE, null);

        eventRepository.events.add(event);

        Scanner scanner = new Scanner("""
                1
                N
                """);

        EventConsoleUI consoleUI = createConsoleUI(eventRepository, scanner);

        consoleUI.deleteEvent();

        assertTrue(eventRepository.findById(new EventId(1)).isPresent());
        assertTrue(output.toString().contains("Deletion cancelled."));
    }

    @Test
    void shouldConfigureWeeklyRecurrenceWithRepeatUntil() {
        FakeEventRepository eventRepository = new FakeEventRepository();

        Event event = createEvent(new EventId(1), RecurrenceType.NONE, null);

        eventRepository.events.add(event);

        LocalDate repeatUntil = LocalDate.now().plusMonths(1);

        Scanner scanner = new Scanner("""
                1
                WEEKLY
                %s
                """.formatted(repeatUntil));

        EventConsoleUI consoleUI = createConsoleUI(eventRepository, scanner);

        consoleUI.configureRecurrence();

        Event updatedEvent = eventRepository.findById(new EventId(1)).orElseThrow();

        assertEquals(RecurrenceType.WEEKLY, updatedEvent.getRecurrenceType());
        assertEquals(repeatUntil, updatedEvent.getRepeatUntil());
    }

    private EventConsoleUI createConsoleUI(FakeEventRepository eventRepository, Scanner scanner) {
        EventService eventService = new EventService(eventRepository, new FakeTaskRepository(), new RecurrenceFactory(),
                List.of());

        return new EventConsoleUI(eventService, null, null, scanner);
    }

    private Event createEvent(EventId eventId, RecurrenceType recurrenceType, LocalDate repeatUntil) {
        return new Event(eventId, "Event", LocalDate.now(), LocalDateTime.now(), recurrenceType, repeatUntil,
                List.of());
    }

    private static class FakeEventRepository implements EventRepository {

        private final List<Event> events = new ArrayList<>();
        private int nextId = 1;

        @Override
        public Event save(Event event) {
            if (event.getEventId() == null) {
                Event savedEvent = new Event(new EventId(nextId++), event.getText(), event.getEventDate(),
                        event.getCreatedAt(), event.getRecurrenceType(), event.getRepeatUntil(), event.getTaskIds());

                events.add(savedEvent);

                return savedEvent;
            }

            events.removeIf(existing -> existing.getEventId().equals(event.getEventId()));

            events.add(event);

            return event;
        }

        @Override
        public Optional<Event> findById(EventId eventId) {
            return events.stream().filter(event -> event.getEventId().equals(eventId)).findFirst();
        }

        @Override
        public List<Event> findAll() {
            return new ArrayList<>(events);
        }

        @Override
        public void deleteById(EventId eventId) {
            events.removeIf(event -> event.getEventId().equals(eventId));
        }
    }

    private static class FakeTaskRepository implements TaskRepository {

        @Override
        public Task save(Task task) {
            return task;
        }

        @Override
        public Optional<Task> findById(TaskId taskId) {
            return Optional.empty();
        }

        @Override
        public List<Task> findAll() {
            return List.of();
        }

        @Override
        public void deleteById(TaskId taskId) {
        }

    }
}

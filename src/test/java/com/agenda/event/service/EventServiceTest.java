package com.agenda.event.service;

import com.agenda.common.exception.EventNotFoundException;
import com.agenda.event.dto.EventDto;
import com.agenda.event.model.Event;
import com.agenda.event.model.EventId;
import com.agenda.event.model.RecurrenceType;
import com.agenda.event.repository.EventRepository;
import com.agenda.task.model.Task;
import com.agenda.task.model.TaskId;
import com.agenda.task.repository.TaskRepository;
import com.agenda.common.exception.TaskNotFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    @Test
    void shouldCreateEvent() {
        EventRepository repository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(repository, taskRepository, new RecurrenceFactory(), List.of());

        EventDto dto = new EventDto(null, "Event", LocalDate.of(2026, 11, 7),
                null, RecurrenceType.NONE, null, List.of());

        EventDto createdEvent = service.create(dto);

        assertNotNull(createdEvent.eventId());
        assertEquals("Event", createdEvent.text());
        assertEquals(LocalDate.of(2026, 11, 7), createdEvent.eventDate());
        assertEquals(RecurrenceType.NONE, createdEvent.recurrenceType());
        assertTrue(createdEvent.taskIds().isEmpty());
    }

    private static class FakeEventRepository implements EventRepository {

        private final List<Event> events = new ArrayList<>();
        private int nextId = 1;

        @Override
        public Event save(Event event) {

            EventId eventId = event.getEventId();

            if (eventId == null) {
                eventId = new EventId(nextId++);
            } else {
                EventId finalEventId = eventId;
                events.removeIf(existingEvent ->
                        existingEvent.getEventId().equals(finalEventId));
            }

            Event savedEvent = new Event(
                    eventId,
                    event.getText(),
                    event.getEventDate(),
                    event.getCreatedAt(),
                    event.getRecurrenceType(),
                    event.getRepeatUntil(),
                    event.getTaskIds()
            );

            events.add(savedEvent);

            return savedEvent;
        }

        @Override
        public Optional<Event> findById(EventId eventId) {
            return events.stream()
                    .filter(event -> event.getEventId().equals(eventId))
                    .findFirst();
        }

        @Override
        public List<Event> findAll() {
            return List.copyOf(events);
        }

        @Override
        public void deleteById(EventId eventId) {
            events.removeIf(event -> event.getEventId().equals(eventId));
        }
    }

    private static class FakeTaskRepository implements TaskRepository {

        private final List<TaskId> taskIds = new ArrayList<>();

        void addTask(TaskId taskId) {
            taskIds.add(taskId);
        }

        @Override
        public Task save(Task task) {
            return task;
        }

        @Override
        public Optional<Task> findById(TaskId taskId) {
            if (taskIds.contains(taskId)) {
                return Optional.of(new Task("Task", LocalDate.now().plusDays(1)));
            }

            return Optional.empty();
        }

        @Override
        public List<Task> findAll() {
            return List.of();
        }

        @Override
        public void deleteById(TaskId taskId) {
            taskIds.remove(taskId);
        }
    }

    private static class FakeEventObserver implements EventObserver {

        private final List<EventDto> notifiedEvents = new ArrayList<>();

        @Override
        public void notify(EventDto event) {
            notifiedEvents.add(event);
        }
    }

    @Test
    void shouldFindEventById() {
        EventRepository repository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(repository, taskRepository, new RecurrenceFactory(), List.of());

        EventDto dto = new EventDto(null, "Event", LocalDate.of(2026, 12, 25),
                null, RecurrenceType.NONE, null, List.of());

        EventDto createdEvent = service.create(dto);

        EventDto foundEvent = service.findById(createdEvent.eventId());

        assertEquals(createdEvent.eventId(), foundEvent.eventId());
        assertEquals("Event", foundEvent.text());
    }

    @Test
    void shouldThrowExceptionWhenEventIsNotFound() {
        EventRepository repository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(repository, taskRepository, new RecurrenceFactory(), List.of());

        assertThrows(EventNotFoundException.class, () -> service.findById(new EventId(9)));
    }

    @Test
    void shouldUpdateEvent() {
        EventRepository repository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(repository, taskRepository, new RecurrenceFactory(), List.of());

        EventDto createdEvent = service.create(new EventDto(null, "Original",
                LocalDate.of(2026, 11, 1), null, RecurrenceType.NONE, null,
                List.of()));

        EventDto updateDto = new EventDto(null, "Updated",
                LocalDate.of(2026, 12, 20), null, RecurrenceType.MONTHLY,
                LocalDate.of(2027, 3, 15), List.of());

        EventDto updatedEvent = service.update(createdEvent.eventId(), updateDto);

        assertEquals("Updated", updatedEvent.text());
        assertEquals(LocalDate.of(2026, 12, 20), updatedEvent.eventDate());
        assertEquals(RecurrenceType.MONTHLY, updatedEvent.recurrenceType());
        assertEquals(LocalDate.of(2027, 3, 15), updatedEvent.repeatUntil());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingEvent() {
        EventRepository repository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(repository, taskRepository, new RecurrenceFactory(), List.of());

        EventDto updateDto = new EventDto(null, "Updated", LocalDate.of(2026, 10,
                1), null, RecurrenceType.MONTHLY,
                LocalDate.of(2026, 12,5 ), List.of());

        assertThrows(EventNotFoundException.class, () -> service.update(new EventId(99), updateDto));
    }

    @Test
    void shouldDeleteEvent() {
        EventRepository repository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(repository, taskRepository, new RecurrenceFactory(), List.of());

        EventDto createdEvent = service.create(new EventDto(null, "Event",
                LocalDate.of(2026, 11, 13), null, RecurrenceType.NONE, null,
                List.of()));

        service.delete(createdEvent.eventId());

        assertThrows(EventNotFoundException.class, () -> service.findById(createdEvent.eventId()));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingEvent() {
        EventRepository repository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(repository, taskRepository, new RecurrenceFactory(), List.of());

        assertThrows(EventNotFoundException.class, () -> service.delete(new EventId(2)));
    }

    @Test
    void shouldFindAllEvents() {
        EventRepository repository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(repository, taskRepository, new RecurrenceFactory(), List.of());

        service.create(new EventDto(null, "Event 1", LocalDate.of(2026, 10, 10),
                null, RecurrenceType.NONE, null, List.of()));

        service.create(new EventDto(null, "Event 2", LocalDate.of(2026, 11, 10),
                null, RecurrenceType.NONE, null, List.of()));

        List<EventDto> events = service.findAll();

        assertEquals(2, events.size());
    }

    @Test
    void shouldThrowExceptionWhenEventRepositoryIsNull() {
        TaskRepository taskRepository = new FakeTaskRepository();

        assertThrows(IllegalArgumentException.class, () ->
                new EventService(null, taskRepository, new RecurrenceFactory(), List.of()));
    }

    @Test
    void shouldThrowExceptionWhenTaskRepositoryIsNull() {
        EventRepository eventRepository = new FakeEventRepository();

        assertThrows(IllegalArgumentException.class, () ->
                new EventService(eventRepository, null, new RecurrenceFactory(), List.of()));
    }

    @Test
    void shouldAddTaskToEvent() {
        EventRepository eventRepository = new FakeEventRepository();
        FakeTaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        EventDto event = service.create(new EventDto(null, "Event", LocalDate.of(2026, 10,
                9), null, RecurrenceType.NONE, null, List.of()));

        TaskId taskId = new TaskId(1);
        taskRepository.addTask(taskId);

        service.addTask(event.eventId(), taskId);

        EventDto updatedEvent = service.findById(event.eventId());

        assertEquals(List.of(taskId), updatedEvent.taskIds());
    }

    @Test
    void shouldThrowExceptionWhenAddingTaskToNonExistingEvent() {
        EventRepository eventRepository = new FakeEventRepository();
        FakeTaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        TaskId taskId = new TaskId(1);

        assertThrows(EventNotFoundException.class, () -> service.addTask(new EventId(7), taskId));
    }

    @Test
    void shouldThrowExceptionWhenTaskDoesNotExist() {
        EventRepository eventRepository = new FakeEventRepository();
        FakeTaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        EventDto event = service.create(new EventDto(null, "Event",
                LocalDate.of(2026, 11, 10), null, RecurrenceType.NONE, null,
                List.of()));

        TaskId taskId = new TaskId(9);

        assertThrows(TaskNotFoundException.class, () -> service.addTask(event.eventId(), taskId));
    }

    @Test
    void shouldThrowExceptionWhenTaskIsAssignedToAnotherEvent() {
        EventRepository eventRepository = new FakeEventRepository();
        FakeTaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        EventDto firstEvent = service.create(new EventDto(null, "Event 1",
                LocalDate.of(2026, 11, 17), null, RecurrenceType.NONE, null,
                List.of()));

        EventDto secondEvent = service.create(new EventDto(null, "Event 2",
                LocalDate.of(2026, 10, 14), null, RecurrenceType.NONE, null,
                List.of()));

        TaskId taskId = new TaskId(1);
        taskRepository.addTask(taskId);

        service.addTask(firstEvent.eventId(), taskId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                service.addTask(secondEvent.eventId(), taskId));

        assertEquals("La tarea solo se puede asociar a un evento.", exception.getMessage());
    }

    @Test
    void shouldNotDuplicateTaskInSameEvent() {
        EventRepository eventRepository = new FakeEventRepository();
        FakeTaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        EventDto event = service.create(new EventDto(null, "Event",
                LocalDate.of(2026, 12, 11), null, RecurrenceType.NONE, null,
                List.of()));

        TaskId taskId = new TaskId(1);
        taskRepository.addTask(taskId);

        service.addTask(event.eventId(), taskId);
        service.addTask(event.eventId(), taskId);

        EventDto updatedEvent = service.findById(event.eventId());

        assertEquals(1, updatedEvent.taskIds().size());
        assertEquals(taskId, updatedEvent.taskIds().getFirst());
    }

    @Test
    void shouldConfigureEventRecurrence() {
        EventRepository eventRepository = new FakeEventRepository();
        FakeTaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        EventDto event = service.create(new EventDto(null, "Event",
                LocalDate.of(2026, 11, 9), null, RecurrenceType.NONE, null,
                List.of()));

        LocalDate repeatUntil = LocalDate.of(2027, 2, 9);

        service.configureRecurrence(event.eventId(), RecurrenceType.MONTHLY, repeatUntil);

        EventDto updatedEvent = service.findById(event.eventId());

        assertEquals(RecurrenceType.MONTHLY, updatedEvent.recurrenceType());
        assertEquals(repeatUntil, updatedEvent.repeatUntil());
    }

    @Test
    void shouldThrowExceptionWhenConfiguringRecurrenceOfNonExistingEvent() {
        EventRepository eventRepository = new FakeEventRepository();
        FakeTaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        assertThrows(EventNotFoundException.class, () -> service.configureRecurrence(new EventId(8),
                        RecurrenceType.MONTHLY, LocalDate.of(2026, 10, 13)));
    }

    @Test
    void shouldThrowExceptionWhenRecurrenceFactoryIsNull() {
        EventRepository eventRepository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();

        assertThrows(IllegalArgumentException.class, () ->
                new EventService(eventRepository, taskRepository, null, List.of()));
    }

    @Test
    void shouldFindUpcomingNonRecurringEvents() {
        EventRepository eventRepository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        service.create(new EventDto(null, "Past event", LocalDate.of(2026, 9, 20),
                null, RecurrenceType.NONE, null, List.of()));

        service.create(new EventDto(null, "Today event", LocalDate.of(2026, 9, 28),
                null, RecurrenceType.NONE, null, List.of()));

        service.create(new EventDto(null, "Future event",
                LocalDate.of(2026, 10, 10), null, RecurrenceType.NONE, null,
                List.of()));

        List<EventDto> result = service.findUpcoming(LocalDate.of(2026, 9, 28));

        assertEquals(2, result.size());
        assertEquals("Today event", result.get(0).text());
        assertEquals("Future event", result.get(1).text());
    }

    @Test
    void shouldFindUpcomingRecurringEvent() {
        EventRepository eventRepository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        EventDto event = service.create(new EventDto(null, "Annual event",
                LocalDate.of(2024, 10, 10), null, RecurrenceType.ANNUAL,
                null, List.of()));

        List<EventDto> result = service.findUpcoming(LocalDate.of(2026, 9, 28));

        assertEquals(1, result.size());
        assertEquals(event.eventId(), result.getFirst().eventId());
    }

    @Test
    void shouldNotFindRecurringEventWhenRecurrenceHasEnded() {
        EventRepository eventRepository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        service.create(new EventDto(null, "Expired annual event",
                LocalDate.of(2024, 10, 10), null, RecurrenceType.ANNUAL,
                LocalDate.of(2025, 10, 10), List.of()));

        List<EventDto> result = service.findUpcoming(LocalDate.of(2026, 9, 28));

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldNotifyObserverOneDayBeforeEvent() {
        EventRepository eventRepository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();

        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        FakeEventObserver observer = new FakeEventObserver();
        service.addObserver(observer);

        EventDto event = service.create(new EventDto(null, "Tomorrow event",
                LocalDate.of(2026, 9, 29), null, RecurrenceType.NONE, null,
                List.of()));

        service.checkUpcomingEvents(LocalDate.of(2026, 9, 28));

        assertEquals(1, observer.notifiedEvents.size());
        assertEquals(event.eventId(), observer.notifiedEvents.getFirst().eventId());
    }

    @Test
    void shouldNotifyRecurringEventOneDayBeforeOccurrence() {
        EventRepository eventRepository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        FakeEventObserver observer = new FakeEventObserver();
        service.addObserver(observer);

        service.create(new EventDto(null, "Annual event", LocalDate.of(2024, 9, 29),
                null, RecurrenceType.ANNUAL, null, List.of()));

        service.checkUpcomingEvents(LocalDate.of(2026, 9, 28));

        assertEquals(1, observer.notifiedEvents.size());
    }

    @Test
    void shouldNotNotifyPastEvent() {
        EventRepository eventRepository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();

        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        FakeEventObserver observer = new FakeEventObserver();
        service.addObserver(observer);

        service.create(new EventDto(null, "Past event", LocalDate.of(2026, 9, 20),
                null, RecurrenceType.NONE, null, List.of()));

        service.checkUpcomingEvents(LocalDate.of(2026, 9, 28));

        assertTrue(observer.notifiedEvents.isEmpty());
    }

    @Test
    void shouldNotifyAllUpcomingEvents() {
        EventRepository eventRepository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        FakeEventObserver observer = new FakeEventObserver();
        service.addObserver(observer);

        service.create(new EventDto(null, "Event 1", LocalDate.of(2026, 11, 16),
                null, RecurrenceType.NONE, null, List.of()));

        service.create(new EventDto(null, "Event 2", LocalDate.of(2026, 11, 16),
                null, RecurrenceType.NONE, null, List.of()));

        service.checkUpcomingEvents(LocalDate.of(2026, 11, 15));

        assertEquals(2, observer.notifiedEvents.size());
    }

    @Test
    void shouldNotifyAllObservers() {
        EventRepository eventRepository = new FakeEventRepository();
        TaskRepository taskRepository = new FakeTaskRepository();

        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        FakeEventObserver firstObserver = new FakeEventObserver();
        FakeEventObserver secondObserver = new FakeEventObserver();

        service.addObserver(firstObserver);
        service.addObserver(secondObserver);

        service.create(new EventDto(null, "Tomorrow event",
                LocalDate.of(2026, 10, 4), null, RecurrenceType.NONE, null,
                List.of()));

        service.checkUpcomingEvents(LocalDate.of(2026, 10, 3));

        assertEquals(1, firstObserver.notifiedEvents.size());
        assertEquals(1, secondObserver.notifiedEvents.size());
    }

    @Test
    void shouldRemoveTaskFromEvent() {
        EventRepository eventRepository = new FakeEventRepository();
        FakeTaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        EventDto event = service.create(new EventDto(null, "Event",
                LocalDate.of(2026, 10, 10), null, RecurrenceType.NONE, null,
                List.of()));

        TaskId taskId = new TaskId(1);
        taskRepository.addTask(taskId);

        service.addTask(event.eventId(), taskId);

        service.removeTask(event.eventId(), taskId);

        EventDto updatedEvent = service.findById(event.eventId());

        assertTrue(updatedEvent.taskIds().isEmpty());
        assertTrue(taskRepository.findById(taskId).isPresent());
    }

    @Test
    void shouldThrowExceptionWhenRemovingTaskFromNonExistingEvent() {
        EventRepository eventRepository = new FakeEventRepository();
        FakeTaskRepository taskRepository = new FakeTaskRepository();
        EventService service = new EventService(eventRepository, taskRepository, new RecurrenceFactory(), List.of());

        assertThrows(EventNotFoundException.class, () ->
                service.removeTask(new EventId(5), new TaskId(1)));
    }

}

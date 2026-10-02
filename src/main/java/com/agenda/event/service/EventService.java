package com.agenda.event.service;

import com.agenda.common.exception.EventNotFoundException;
import com.agenda.event.dto.EventDto;
import com.agenda.event.model.Event;
import com.agenda.event.model.EventId;
import com.agenda.event.model.RecurrenceType;
import com.agenda.event.repository.EventRepository;
import com.agenda.task.repository.TaskRepository;
import com.agenda.common.exception.TaskNotFoundException;
import com.agenda.task.model.TaskId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventService {
    private final EventRepository eventRepository;
    private final TaskRepository taskRepository;
    private final RecurrenceFactory recurrenceFactory;
    private final List<EventObserver> observers;

    public EventService(EventRepository eventRepository, TaskRepository taskRepository,
                        RecurrenceFactory recurrenceFactory, List<EventObserver> observers) {
        if (eventRepository == null) {
            throw new IllegalArgumentException("EventRepository must not be NULL");
        }

        if (taskRepository == null) {
            throw new IllegalArgumentException("TaskRepository must not be NULL");
        }

        if (recurrenceFactory == null) {
            throw new IllegalArgumentException("RecurrenceFactory must not be NULL");
        }

        if (observers == null) {
            throw new IllegalArgumentException("Observers must not be NULL");
        }

        this.eventRepository = eventRepository;
        this.taskRepository = taskRepository;
        this.recurrenceFactory = recurrenceFactory;
        this.observers = new ArrayList<>(observers);
    }

    private EventDto toDto(Event event) {
        return new EventDto(event.getEventId(), event.getText(), event.getEventDate(), event.getCreatedAt(),
                event.getRecurrenceType(), event.getRepeatUntil(), event.getTaskIds());
    }

    public EventDto create(EventDto dto) {
        Event event = new Event(dto.text(), dto.eventDate());

        event.configureRecurrence(dto.recurrenceType(), dto.repeatUntil());

        Event savedEvent = eventRepository.save(event);

        return toDto(savedEvent);
    }

    public EventDto findById(EventId eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException("Event with ID not found"));

        return toDto(event);
    }

    public EventDto update(EventId eventId, EventDto dto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException("Event with ID not found"));

        event.updateDetails(dto.text(), dto.eventDate());

        event.configureRecurrence(dto.recurrenceType(), dto.repeatUntil());

        Event updatedEvent = eventRepository.save(event);

        return toDto(updatedEvent);
    }

    public void delete(EventId eventId) {
        eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException("Event with ID not found"));

        eventRepository.deleteById(eventId);
    }

    public List<EventDto> findAll() {
        return eventRepository.findAll().stream().map(this::toDto).toList();
    }

    public void addTask(EventId eventId, TaskId taskId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException("Event with ID not found"));

        taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task with ID not found"));

        boolean taskAssignedToAnotherEvent = eventRepository.findAll().stream()
                .filter(existingEvent -> !existingEvent.getEventId().equals(eventId))
                .anyMatch(existingEvent -> existingEvent.getTaskIds().contains(taskId));

        if (taskAssignedToAnotherEvent) {
            throw new IllegalArgumentException("La tarea solo se puede asociar a un evento.");
        }

        event.addTask(taskId);

        eventRepository.save(event);
    }

    public void configureRecurrence(EventId eventId, RecurrenceType recurrenceType, LocalDate repeatUntil) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException("Event with ID not found"));

        event.configureRecurrence(recurrenceType, repeatUntil);

        eventRepository.save(event);
    }

    public List<EventDto> findUpcoming(LocalDate date) {
        List<EventDto> upcomingEvents = new ArrayList<>();

        for (Event event : eventRepository.findAll()) {
            RecurrencePolicy policy = recurrenceFactory.create(event.getRecurrenceType());

            LocalDate nextDate = policy.nextDate(event.getEventDate(), date, event.getRepeatUntil());

            if (nextDate != null) {
                upcomingEvents.add(new EventDto(event.getEventId(), event.getText(), nextDate, event.getCreatedAt(),
                        event.getRecurrenceType(), event.getRepeatUntil(), event.getTaskIds()));
            }
        }

        return upcomingEvents;
    }

    public void addObserver(EventObserver observer) {
        observers.add(observer);
    }

    public void checkUpcomingEvents(LocalDate date) {
        LocalDate notificationDate = date.plusDays(1);

        eventRepository.findAll().forEach(event -> {
            RecurrencePolicy policy = recurrenceFactory.create(event.getRecurrenceType());

            LocalDate nextDate = policy.nextDate(event.getEventDate(), notificationDate, event.getRepeatUntil());

            if (notificationDate.equals(nextDate)) {
                EventDto dto = new EventDto(event.getEventId(), event.getText(), nextDate, event.getCreatedAt(),
                        event.getRecurrenceType(), event.getRepeatUntil(), event.getTaskIds());

                observers.forEach(observer -> observer.notify(dto));
            }
        });
    }

    public void removeTask(EventId eventId, TaskId taskId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException("Event with ID not found"));

        event.removeTask(taskId);

        eventRepository.save(event);
    }

}

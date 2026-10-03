package com.agenda.event.cli;

import com.agenda.event.dto.EventDto;
import com.agenda.event.model.EventId;
import com.agenda.event.model.RecurrenceType;
import com.agenda.event.service.EventService;
import com.agenda.note.dto.NoteDto;
import com.agenda.note.service.NoteService;
import com.agenda.task.dto.TaskDto;
import com.agenda.task.model.TaskId;
import com.agenda.task.service.TaskService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class EventConsoleUI {
    private final EventService eventService;
    private final TaskService taskService;
    private final NoteService noteService;
    private final Scanner scanner;

    public EventConsoleUI(EventService eventService, TaskService taskService, NoteService noteService,
                          Scanner scanner) {

        this.eventService = eventService;
        this.taskService = taskService;
        this.noteService = noteService;
        this.scanner = scanner;
    }

    public void showMenu() {
        System.out.println("""
            
            --- EVENTS ---
            1. Create event
            2. Update event
            3. Delete event
            4. List all events
            5. List upcoming events
            6. Add task to event
            7. Remove task from event
            8. Configure recurrence
            0. Return to main menu
            """);
    }

    private RecurrenceType readRecurrenceType() {
        String input = scanner.nextLine().trim();

        if (input.isBlank()) {
            return RecurrenceType.NONE;
        }

        return RecurrenceType.valueOf(input.toUpperCase());
    }

    public void createEvent() {
        System.out.print("Text: ");
        String text = scanner.nextLine();

        System.out.print("Event date (YYYY-MM-DD): ");
        LocalDate eventDate = LocalDate.parse(scanner.nextLine());

        if (eventDate.isBefore(LocalDate.now())) {
            System.out.print("The event has already taken place. Do you want to register it anyway? (Y/N): ");

            if (!scanner.nextLine().equalsIgnoreCase("Y")) {
                System.out.println("Event creation cancelled.");
                return;
            }
        }

        System.out.print("Recurrence (WEEKLY, MONTHLY, ANNUAL, press Enter for NONE): ");
        RecurrenceType recurrenceType = readRecurrenceType();

        LocalDate repeatUntil = null;

        if (recurrenceType != RecurrenceType.NONE) {
            System.out.print("Repeat until (YYYY-MM-DD, press Enter for no end date): ");
            String repeatUntilInput = scanner.nextLine();

            if (!repeatUntilInput.isBlank()) {
                repeatUntil = LocalDate.parse(repeatUntilInput);
            }
        }

        EventDto dto = new EventDto(null, text, eventDate, null, recurrenceType, repeatUntil,
                List.of());

        EventDto createdEvent = eventService.create(dto);

        System.out.println("Event created successfully. ID: " + createdEvent.eventId().value());
    }

    public void updateEvent() {
        System.out.print("Event ID: ");
        EventId eventId = new EventId(Integer.parseInt(scanner.nextLine()));

        EventDto currentEvent = eventService.findById(eventId);

        System.out.println("Current text: " + currentEvent.text());
        System.out.print("New text (press Enter to keep current): ");
        String text = scanner.nextLine();

        if (text.isBlank()) {
            text = currentEvent.text();
        }

        System.out.println("Current date: " + currentEvent.eventDate());
        System.out.print("New date (YYYY-MM-DD, press Enter to keep current): ");
        String dateInput = scanner.nextLine();

        LocalDate eventDate = currentEvent.eventDate();

        if (!dateInput.isBlank()) {
            eventDate = LocalDate.parse(dateInput);
        }

        EventDto updatedEvent = new EventDto(currentEvent.eventId(), text, eventDate, currentEvent.createdAt(),
                currentEvent.recurrenceType(), currentEvent.repeatUntil(), currentEvent.taskIds());

        eventService.update(eventId, updatedEvent);

        System.out.println("Event updated successfully.");
    }

    public void deleteEvent() {
        System.out.print("Event ID: ");
        EventId eventId = new EventId(Integer.parseInt(scanner.nextLine()));

        EventDto event = eventService.findById(eventId);

        System.out.println("Event: " + event.text());
        System.out.print("Are you sure you want to delete this event? (Y/N): ");
        String confirmation = scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        eventService.delete(eventId);

        System.out.println("Event deleted successfully.");
    }

    public void listEvents() {
        List<EventDto> events = eventService.findAll();

        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }

        events.forEach(this::printEvent);
    }

    public void listUpcomingEvents() {
        System.out.print("Use current date? (Y/N): ");
        String useCurrentDate = scanner.nextLine();

        LocalDate date;

        if (useCurrentDate.equalsIgnoreCase("Y")) {
            date = LocalDate.now();
        } else {
            System.out.print("Date (YYYY-MM-DD): ");
            date = LocalDate.parse(scanner.nextLine());
        }

        List<EventDto> events = eventService.findUpcoming(date);

        if (events.isEmpty()) {
            System.out.println("No upcoming events found for this date.");
            return;
        }

        events.forEach(this::printEvent);
    }

    public void addTaskToEvent() {
        System.out.print("Event ID: ");
        EventId eventId = new EventId(Integer.parseInt(scanner.nextLine()));

        System.out.print("Task ID: ");
        TaskId taskId = new TaskId(Integer.parseInt(scanner.nextLine()));

        eventService.addTask(eventId, taskId);

        System.out.println("Task added to event successfully.");
    }

    public void removeTaskFromEvent() {
        System.out.print("Event ID: ");
        EventId eventId = new EventId(Integer.parseInt(scanner.nextLine()));

        System.out.print("Task ID: ");
        TaskId taskId = new TaskId(Integer.parseInt(scanner.nextLine()));

        eventService.removeTask(eventId, taskId);

        System.out.println("Task removed from event successfully.");
    }

    public void configureRecurrence() {
        System.out.print("Event ID: ");
        EventId eventId = new EventId(Integer.parseInt(scanner.nextLine()));

        System.out.print("Recurrence (NONE, WEEKLY, MONTHLY, ANNUAL): ");
        RecurrenceType recurrenceType = RecurrenceType.valueOf(scanner.nextLine().trim().toUpperCase());

        LocalDate repeatUntil = null;

        if (recurrenceType != RecurrenceType.NONE) {
            System.out.print("Repeat until (YYYY-MM-DD, press Enter for no end date): ");
            String repeatUntilInput = scanner.nextLine();

            if (!repeatUntilInput.isBlank()) {
                repeatUntil = LocalDate.parse(repeatUntilInput);
            }
        }

        eventService.configureRecurrence(eventId, recurrenceType, repeatUntil);

        System.out.println("Event recurrence configured successfully.");
    }

    private void printEvent(EventDto event) {
        System.out.println("ID: " + event.eventId().value() + "\nText: " + event.text() +
                "\nDate: " + event.eventDate() + "\nRecurrence: " + event.recurrenceType());

        if (event.taskIds().isEmpty()) {
            System.out.println("Tasks: ");
        } else {
            System.out.println("Tasks:");

            for (TaskId taskId : event.taskIds()) {
                TaskDto task = taskService.findById(taskId);

                System.out.println("  - Task ID: " + task.taskId().value() + " | " + task.text());

                List<NoteDto> notes = noteService.findByTaskId(taskId);

                for (NoteDto note : notes) {
                    System.out.println("      Note ID: " + note.noteId().value() + " | " + note.content());
                }
            }
        }

        System.out.println();
    }
}

package com.agenda.task.cli;

import com.agenda.note.dto.NoteDto;
import com.agenda.task.dto.TaskDto;
import com.agenda.task.model.*;
import com.agenda.task.service.TaskService;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class TaskConsoleUI {
    private final TaskService service;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public TaskConsoleUI(TaskService taskService, Scanner scanner) {
        this.service = taskService;
        this.scanner = scanner;
    }

    public void showMenu() {
        System.out.println("""
          
            --- TASK ---
            1. Create task
            2. Update task
            3. Delete task
            4. List all
            5. Mark as completed
            6. List pending
            7. List completed
            8. Filter by priority
            9. Filter by status
            10. Filter by date
            0. Back
            """);
    }

    public void createTask() {
        System.out.print("Text: ");
        String text = scanner.nextLine();

        System.out.print("Priority (LOW, MEDIUM, HIGH, press Enter for MEDIUM): ");
            TaskPriority priority = readPriority();

            System.out.print("Expiration date (dd-MM-yyyy, press Enter for none): ");
            LocalDate expirationDate = readDate();

        TaskDto taskDto = new TaskDto(null, text, priority, null, expirationDate,
                null, null, null);

        TaskDto createdTask = this.service.create(taskDto);

        System.out.format("Task created successfully. ID: %d", createdTask.id().value());
    }

    public void updateTask() {
        System.out.print("Task ID:");

        TaskId taskId = new TaskId(Integer.parseInt(scanner.nextLine()));

        System.out.print("New text (Press Enter to skip): ");

        String text = scanner.nextLine();

        System.out.print("New priority (Press Enter to skip");

        TaskPriority priority = readPriority();

        System.out.print("New expiration date (Press Enter to skip");

        LocalDate expirationDate = readDate();

        TaskDto task = new TaskDto(null, text, priority, null,
                expirationDate, null, null, null);

        TaskDto updatedTask = service.update(taskId, task);

        System.out.println("Note updated successfully");
    }

    public void deleteTask() {

        System.out.print("Task ID: ");

        TaskId taskId = new TaskId(Integer.parseInt(scanner.nextLine()));

        TaskDto task = service.findById(taskId);

        System.out.format("Task: %s.\nAre you sure you want to delete this task? (Y/N)\n", task.text());
        String confirmation = scanner.nextLine().trim();

        if(!confirmation.equalsIgnoreCase("y")) {
            System.out.print("Deletion canceled");
            return;
        }
        service.delete(taskId);
        System.out.print("Task deleted successfully");
    }

    public void completeTask() {
        System.out.print("Task ID: ");

        TaskId taskId = new TaskId(Integer.parseInt(scanner.nextLine()));

        TaskDto task = service.findById(taskId);

        System.out.format("Task: %s.\nMark as completed? (Y/N)\n", task.text());

        String confirmation = scanner.nextLine().trim();

        if(!confirmation.equalsIgnoreCase("y")) {
            return;
        }

        service.complete(taskId);
        System.out.print("Marked as Complete");
    }

    public void listTasks() {
        List<TaskDto> tasks = service.findAll();

        if(tasks.isEmpty()) {
            System.out.print("No tasks found");
            return;
        }
        taskLister(tasks);
    }

    public void listPendingTasks() {
        List<TaskDto> pendingTasks = service.findPending();
        if(pendingTasks.isEmpty()) {
            System.out.print("No pending tasks found");
            return;
        }
        taskLister(pendingTasks);
    }

    public void listTasksByPriority() {
        System.out.print("Task priority (LOW, MEDIUM, HIGH)");

        TaskPriority userTaskPriority = readPriority();

        if(userTaskPriority == null) {
            throw new IllegalArgumentException("Task priority must be LOW, MEDIUM or HIGH");
        }
        List<TaskDto> tasksByPriority = service.filterByPriority(userTaskPriority);

        if(tasksByPriority.isEmpty()) {
            System.out.format("No tasks found with %s priority", userTaskPriority.name());
        }
        taskLister(tasksByPriority);
    }

    public void listTasksByStatus() {
        System.out.print("Task status (PENDING, COMPLETED)");

        TaskStatus userTaskStatus = readStatus();

        if(userTaskStatus == null) {
            throw new IllegalArgumentException("Task status must be PENDING or COMPLETED");
        }

        List<TaskDto> tasksByStatus = service.filterByStatus(userTaskStatus);

        if(tasksByStatus.isEmpty()) {
            System.out.format("No tasks found with %s status", userTaskStatus.name());
        }
    }

    public void listTasksByDate() {
        System.out.print("Task date (dd-MM-yyyy):");

        LocalDate userTaskDate = readDate();

        if(userTaskDate == null) {
            return;
        }

        List<TaskDto> listsByDate = service.filterByDate(userTaskDate);

        if(listsByDate.isEmpty()) {
            System.out.format("No tasks found on %s", userTaskDate);
        }
    }

    public void listSortedTasks() {
        System.out.print("Sort by (PRIORITY, STATUS, DATE");

        String userChoice = scanner.nextLine().trim().toLowerCase();

        TaskSortStrategy sortStrategy;

        switch (userChoice) {
            case "priority": {
                sortStrategy = new PrioritySortStrategy();
                break;
            }
            case "status": {
                sortStrategy = new StatusSortStrategy();
                break;
            }
            case "date": {
                sortStrategy = new DateSortStrategy();
                break;
            }
            default: throw new IllegalArgumentException("Sort option must be PRIORITY, STATUS or DATE");
        }

        List<TaskDto> sortedTasks = service.sortTasks(sortStrategy);

        if(sortedTasks.isEmpty()) {
            System.out.print("No tasks found");
        }
        listTasks();
    }



    private static void taskLister(List<TaskDto> pendingTasks) {
        pendingTasks.forEach(taskDto -> System.out.format(
                "ID: %d\nText: %s\nCreated at: %s\nEvent ID: %d",
                taskDto.id().value(), taskDto.text(), taskDto.createdAt(), taskDto.eventId().value()));
    }

    private LocalDate readDate() {
        String userInput = scanner.nextLine().trim();

        if(userInput.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(userInput, DATE_FORMATTER);
        } catch(DateTimeException e) {
            System.out.println("Date must follow the pattern dd-MM-yyyy");
        }
        return null;
    }

    private TaskPriority readPriority() {
        String userTaskPriority = scanner.nextLine().trim().toLowerCase();

        if(userTaskPriority.isBlank()) {
            return null;
        }
        return switch (userTaskPriority) {
            case "low" -> TaskPriority.LOW;
            case "medium" -> TaskPriority.MEDIUM;
            case "high" -> TaskPriority.HIGH;
            default -> throw new IllegalArgumentException("Task priority must be LOW, MEDUM or HIGH");
        };
    }

    private TaskStatus readStatus() {
        String userTaskStatus = scanner.nextLine().trim().toLowerCase();

        if(userTaskStatus.isBlank()) {
            return null;
        }
        return switch (userTaskStatus) {
            case "pending" -> TaskStatus.PENDING;
            case "completed" -> TaskStatus.COMPLETED;
            default -> throw new IllegalArgumentException("Task status must be PENDING or COMPLETED");
        };
    }
}

package com.agenda.task.cli;

import com.agenda.task.dto.TaskDto;
import com.agenda.task.model.TaskId;
import com.agenda.task.model.TaskPriority;
import com.agenda.task.service.TaskService;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        String userPriority = scanner.nextLine().trim().toLowerCase();

        if(userPriority.isBlank()) {
            return null;
        }

        return switch (userPriority) {
            case "low" -> TaskPriority.LOW;
            case "medium" -> TaskPriority.MEDIUM;
            case "high" -> TaskPriority.HIGH;
            default -> throw new IllegalArgumentException("User priority must be LOW, MEDUM or HIGH");
        };
    }
}

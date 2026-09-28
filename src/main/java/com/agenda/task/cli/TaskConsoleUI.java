package com.agenda.task.cli;

import com.agenda.task.model.TaskPriority;
import com.agenda.task.model.TaskStatus;
import com.agenda.task.service.TaskService;

import java.util.Scanner;

public class TaskConsoleUI {
    private final TaskService taskService;
    private final Scanner scanner;

    public TaskConsoleUI(TaskService taskService, Scanner scanner) {
        this.taskService = taskService;
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

        System.out.print("Priority (LOW, MEDIUM, HIGH): ");
            TaskPriority priority = readPriority();

            System.out.print("Status (PENDING, COMPLETED)");
        TaskStatus status = readStatus();
    }

    private TaskStatus readStatus() {
        String userStatus = scanner.nextLine();
        userStatus.toLowerCase();

        switch(userStatus) {
            case "pending": {
                return TaskStatus.PENDING;
            }
            case "completed": {
                return TaskStatus.COMPLETED;
            }
            default: return null;
        }
    }

    private TaskPriority readPriority() {
        String userPriority = scanner.nextLine();
        userPriority.toLowerCase();

        switch (userPriority) {
            case "low": {
                return TaskPriority.LOW;
            }
            case "medium": {
                return TaskPriority.MEDIUM;
            }
            case "high": {
                return TaskPriority.HIGH;
            }
            default: return null;
        }
    }




}

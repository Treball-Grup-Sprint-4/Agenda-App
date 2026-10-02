package com.agenda.task.cli;

import com.agenda.common.utils.ConsoleExceptionHandler;
import com.agenda.task.dto.TaskDto;
import com.agenda.task.model.*;
import com.agenda.task.service.TaskService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class TaskConsoleUI {
    private final TaskService service;
    private final Scanner scanner;

    public TaskConsoleUI(TaskService taskService, Scanner scanner) {
        this.service = taskService;
        this.scanner = scanner;
    }

    public void runTaskConsole() {

        boolean exit = false;

        while(!exit) {

            showMenu();

            System.out.print("Choose an option");
            String userOption = scanner.nextLine();


            switch(userOption.trim()) {
                case "1": {
                    ConsoleExceptionHandler.execute(()->this.createTask());
                    break;
                }
                case "2": {
                    ConsoleExceptionHandler.execute(()->updateTask());
                    break;
                }
                case "3": {
                    ConsoleExceptionHandler.execute(()->deleteTask());
                    break;
                }
                case "4": {
                    ConsoleExceptionHandler.execute(()->listTasks());
                    break;
                }
                case "5": {
                    ConsoleExceptionHandler.execute(()->completeTask());
                    break;
                }
                case "6": {
                    ConsoleExceptionHandler.execute(()->listPendingTasks());
                    break;
                }
                case "7": {
                    ConsoleExceptionHandler.execute(()->listCompletedTasks());
                    break;
                }
                case "8": {
                    ConsoleExceptionHandler.execute(()->listTasksByPriority());
                    break;
                }
                case "9": {
                    ConsoleExceptionHandler.execute(()->listTasksByStatus());
                    break;
                }
                case "10": {
                    ConsoleExceptionHandler.execute(()->listTasksByDate());
                    break;
                }
                case "11": {
                    ConsoleExceptionHandler.execute(()->sortTasks());
                    break;
                }
                case "0": {
                    exit = true;
                    break;
                }
                default: System.out.println("Wrong option (choose a number from 0 to 11)");
            }
        }
    }

    private void showMenu() {
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
            11. Sort tasks
            0. Back
            """);
    }

    private void createTask() {
        System.out.print("Text: ");
        String text = scanner.nextLine();

        System.out.print("Priority (LOW, MEDIUM, HIGH, press Enter for MEDIUM): ");
            TaskPriority priority = readPriority();

            System.out.print("Expiration date (yyyy-MM-dd, press Enter for none): ");
            LocalDate expirationDate = readDate();

        TaskDto taskDto = new TaskDto(null, text, priority, null, expirationDate,
                null, null);

        TaskDto createdTask = this.service.create(taskDto);

        System.out.format("Task created successfully. ID: %d\n", createdTask.taskId().value());
    }

    private void updateTask() {
        System.out.print("Task ID:");

        TaskId taskId = new TaskId(Integer.parseInt(scanner.nextLine()));

        TaskDto currentTask = service.findById(taskId);

        System.out.format("Current text:\n%s\n", currentTask.text());
        System.out.println("New text (Press Enter to skip):");

        String text = scanner.nextLine();

        if(text.isBlank()) {
            text = currentTask.text();
        }
        System.out.format("Current priority:\n%s\n", currentTask.priority().name());
        System.out.println("New priority (Press Enter to skip)");

        TaskPriority priority = readPriority();

        System.out.print("Current expiration date: ");

        if(currentTask.expirationDate() == null) {
            System.out.println("-");
        } else {
            System.out.format("%s\n", currentTask.expirationDate());
        }
        System.out.println("New expiration date (yyyy-MM-dd, Enter to keep, - to remove):");

        String dateInput = scanner.nextLine().trim();

        LocalDate expirationDate;

        if (dateInput.isBlank()) {
            expirationDate = currentTask.expirationDate();
        } else if (dateInput.equals("-")) {
            expirationDate = null;
        } else {
            expirationDate = LocalDate.parse(dateInput);
        }

        TaskDto task = new TaskDto(null, text, priority, null,
                expirationDate, null, null);

        service.update(taskId, task);

        System.out.println("Task updated successfully");
    }

    private void deleteTask() {

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

    private void completeTask() {
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

    private void listTasks() {
        List<TaskDto> tasks = service.findAll();

        if(tasks.isEmpty()) {
            System.out.print("No tasks found");
            return;
        }
        taskLister(tasks);
    }

    private void listPendingTasks() {
        List<TaskDto> pendingTasks = service.findPending();
        if(pendingTasks.isEmpty()) {
            System.out.print("No pending tasks found");
            return;
        }
        taskLister(pendingTasks);
    }

    private void listCompletedTasks() {
        List<TaskDto> completedTasks = service.findCompleted();
        if(completedTasks.isEmpty()) {
            System.out.print("No completed tasks found");
            return;
        }
        taskLister(completedTasks);
    }

    private void listTasksByPriority() {
        System.out.print("Task priority (LOW, MEDIUM, HIGH)");

        TaskPriority userTaskPriority = readPriority();

        if(userTaskPriority == null) {
            throw new IllegalArgumentException("Task priority must be LOW, MEDIUM or HIGH");
        }
        List<TaskDto> tasksByPriority = service.filterByPriority(userTaskPriority);

        if(tasksByPriority.isEmpty()) {
            System.out.format("No tasks found with %s priority", userTaskPriority.name());
            return;
        }
        taskLister(tasksByPriority);
    }

    private void listTasksByStatus() {
        System.out.print("Task status (PENDING, COMPLETED)");

        TaskStatus userTaskStatus = readStatus();

        if(userTaskStatus == null) {
            throw new IllegalArgumentException("Task status must be PENDING or COMPLETED");
        }

        List<TaskDto> tasksByStatus = service.filterByStatus(userTaskStatus);

        if(tasksByStatus.isEmpty()) {
            System.out.format("No tasks found with %s status", userTaskStatus.name());
            return;
        }
        taskLister(tasksByStatus);
    }

    private void listTasksByDate() {
        System.out.print("Task date (yyyy-MM-dd):");

        LocalDate userTaskDate = readDate();

        if(userTaskDate == null) {
            return;
        }

        List<TaskDto> listsByDate = service.filterByDate(userTaskDate);

        if(listsByDate.isEmpty()) {
            System.out.format("No tasks found on %s", userTaskDate);
            return;
        }

        taskLister(listsByDate);
    }

    private void sortTasks() {
        System.out.print("Sort by (PRIORITY, STATUS, DATE)");

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
            return;
        }
        taskLister(sortedTasks);
    }


    private static void taskLister(List<TaskDto> tasks) {
        tasks.forEach((taskDto) -> { System.out.format(
                "ID: %d\nText: %s\nCreated at: %s\nPriority: %s\nStatus: %s\n",
                taskDto.taskId().value(),
                taskDto.text(),
                taskDto.createdAt(),
                taskDto.priority().name(),
                taskDto.status().name());
            System.out.print("Expiration date: ");
            if(taskDto.expirationDate() == null) {
                System.out.println("-");
            } else {
                System.out.format("%s\n", taskDto.expirationDate());
            }
            System.out.print("Completed at: ");
            if(taskDto.completedAt() == null) {
                System.out.println("-");
            } else {
                System.out.format("%s\n", taskDto.completedAt());
            }
            System.out.println();
        });
    }

    private LocalDate readDate() {
        String userInput = scanner.nextLine().trim();

        if(userInput.isBlank()) {
            return null;
        }
        return LocalDate.parse(userInput);
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
            default -> throw new IllegalArgumentException("Task priority must be LOW, MEDIUM or HIGH");
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

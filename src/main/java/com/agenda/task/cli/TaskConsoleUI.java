package com.agenda.task.cli;

import com.agenda.task.service.TaskService;

import java.util.Scanner;

public class TaskConsoleUI {
    private final TaskService taskService;
    private final Scanner taskScanner;

    public TaskConsoleUI(TaskService taskService, Scanner scanner) {
        this.taskService = taskService;
        this.taskScanner = scanner;
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

    
}

package com.agenda.application.menu;

import com.agenda.common.utils.ConsoleExceptionHandler;
import com.agenda.event.cli.EventConsoleUI;
import com.agenda.note.cli.NoteConsoleUI;
import com.agenda.task.cli.TaskConsoleUI;

import java.util.Scanner;

public class ApplicationMenu {

    private final NoteConsoleUI noteConsoleUI;
    private final TaskConsoleUI taskConsoleUI;
    private final EventConsoleUI eventConsoleUI;
    private final Scanner scanner;

    public ApplicationMenu(NoteConsoleUI noteConsoleUI,
                           TaskConsoleUI taskConsoleUI,
                           EventConsoleUI eventConsoleUI,
                           Scanner scanner) {
        this.noteConsoleUI = noteConsoleUI;
        this.taskConsoleUI = taskConsoleUI;
        this.eventConsoleUI = eventConsoleUI;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;

        while (running) {
            showMainMenu();

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> noteMenu();
                case "2" -> taskMenu();
                case "3" -> eventMenu();
                case "0" -> running = false;
                default -> executeAndContinue(() -> {
                    throw new IllegalArgumentException("Invalid option.");
                });
            }
        }

        System.out.println("Program closed.");
    }

    private void showMainMenu() {
        System.out.println("""
                
                --- AGENDA ---
                1. Note
                2. Task
                3. Event
                0. App close
                """);

        System.out.print("Select an option: ");
    }

    private void noteMenu() {
        boolean running = true;

        while (running) {
            noteConsoleUI.showMenu();

            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> executeAndContinue(noteConsoleUI::createNote);
                case "2" -> executeAndContinue(noteConsoleUI::updateNote);
                case "3" -> executeAndContinue(noteConsoleUI::deleteNote);
                case "4" -> executeAndContinue(noteConsoleUI::listNotes);
                case "5" -> executeAndContinue(noteConsoleUI::listNotesByTask);
                case "0" -> running = false;
                default -> executeAndContinue(() -> {
                    throw new IllegalArgumentException("Invalid option.");
                });
            }
        }
    }

    private void taskMenu() {
        boolean running = true;

        while (running) {
            taskConsoleUI.showMenu();

            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> executeAndContinue(taskConsoleUI::createTask);
                case "2" -> executeAndContinue(taskConsoleUI::updateTask);
                case "3" -> executeAndContinue(taskConsoleUI::deleteTask);
                case "4" -> executeAndContinue(taskConsoleUI::listTasks);
                case "5" -> executeAndContinue(taskConsoleUI::completeTask);
                case "6" -> executeAndContinue(taskConsoleUI::listPendingTasks);
                case "7" -> executeAndContinue(taskConsoleUI::listCompletedTasks);
                case "8" -> executeAndContinue(taskConsoleUI::listTasksByPriority);
                case "9" -> executeAndContinue(taskConsoleUI::listTasksByStatus);
                case "10" -> executeAndContinue(taskConsoleUI::listTasksByDate);
                case "11" -> executeAndContinue(taskConsoleUI::sortTasks);
                case "0" -> running = false;
                default -> executeAndContinue(() -> {
                    throw new IllegalArgumentException("Invalid option.");
                });
            }
        }
    }

    private void eventMenu() {
        boolean running = true;

        while (running) {
            eventConsoleUI.showMenu();

            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> executeAndContinue(eventConsoleUI::createEvent);
                case "2" -> executeAndContinue(eventConsoleUI::updateEvent);
                case "3" -> executeAndContinue(eventConsoleUI::deleteEvent);
                case "4" -> executeAndContinue(eventConsoleUI::listUpcomingEvents);
                case "5" -> executeAndContinue(eventConsoleUI::addTaskToEvent);
                case "6" -> executeAndContinue(eventConsoleUI::removeTaskFromEvent);
                case "7" -> executeAndContinue(eventConsoleUI::configureRecurrence);
                case "0" -> running = false;
                default -> executeAndContinue(() -> {
                    throw new IllegalArgumentException("Invalid option.");
                });
            }
        }
    }

    private void executeAndContinue(Runnable action) {
        ConsoleExceptionHandler.execute(action);
        pressEnterToContinue();
    }

    private void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

}

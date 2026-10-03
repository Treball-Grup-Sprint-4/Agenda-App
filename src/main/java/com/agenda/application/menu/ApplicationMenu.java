package com.agenda.application.menu;

import com.agenda.event.cli.EventConsoleUI;
import com.agenda.note.cli.NoteConsoleUI;
import com.agenda.task.cli.TaskConsoleUI;

import java.util.Scanner;

public class ApplicationMenu {
    private final Scanner scanner;
    private final EventConsoleUI eventConsoleUi;
    private final TaskConsoleUI taskConsoleUI;
    private final NoteConsoleUI noteConsoleUI;

    public ApplicationMenu(Scanner scanner, EventConsoleUI eventConsoleUi, TaskConsoleUI taskConsoleUI, NoteConsoleUI noteConsoleUI) {
        this.scanner = scanner;
        this.eventConsoleUi = eventConsoleUi;
        this.taskConsoleUI = taskConsoleUI;
        this.noteConsoleUI = noteConsoleUI;
    }

    public void displayAppMenu() {
        boolean exit = false;

        while(!exit) {
            System.out.println("""
            
            --- AGENDA APP ---
            1. Events
            2. Tasks
            3. Notes
            0. Exit
            """);

            System.out.println("Choose an option:");
            String userOption = scanner.nextLine().trim();

            switch(userOption) {
                case "1": {
                    // eventConsoleUi.runEventConsole();
                    break;
                }
                case "2": {
                    taskConsoleUI.runTaskConsole();
                    break;
                }
                case "3": {
                    // noteConsoleUI.runNoteConsole();
                    break;
                }
                case "0": {
                    exit = true;
                    break;
                }
                default: System.out.println("Wrong option (choose a number between 0 and 3)");
            }
        }
    }
}

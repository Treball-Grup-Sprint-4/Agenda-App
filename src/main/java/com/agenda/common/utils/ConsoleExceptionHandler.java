package com.agenda.common.utils;

import com.agenda.common.exception.NoteNotFoundException;
import com.agenda.common.exception.TaskNotFoundException;
import com.agenda.common.exception.PersistenceException;

public final class ConsoleExceptionHandler {

    private ConsoleExceptionHandler() {
    }

    public static void execute(Runnable action) {
        try {
            action.run();

        } catch (NumberFormatException e) {
            System.out.println("Input must be a valid number.");

        } catch (NoteNotFoundException | TaskNotFoundException | IllegalArgumentException e) {
            System.out.println(e.getMessage());

        } catch (PersistenceException e) {
            System.out.println("Database error. Please try again.");
        }
    }

}


package com.agenda.application.menu;

import com.agenda.event.cli.EventConsoleUI;
import com.agenda.note.cli.NoteConsoleUI;
import com.agenda.task.cli.TaskConsoleUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationMenuTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private ApplicationMenu createMenu(Scanner scanner) {
        NoteConsoleUI noteConsoleUI = new NoteConsoleUI(null, scanner);
        TaskConsoleUI taskConsoleUI = new TaskConsoleUI(null, scanner);
        EventConsoleUI eventConsoleUI = new EventConsoleUI(null, scanner);

        return new ApplicationMenu(noteConsoleUI, taskConsoleUI,eventConsoleUI, scanner);
    }

    @Test
    void shouldCloseApplicationWhenOptionIsZero() {
        Scanner scanner = new Scanner("0\n");

        ApplicationMenu menu = createMenu(scanner);

        menu.run();

        assertTrue(output.toString().contains("Program closed."));
    }

    @Test
    void shouldContinueWhenMainMenuOptionIsInvalid() {
        Scanner scanner = new Scanner("""
                99
                
                0
                """);

        ApplicationMenu menu = createMenu(scanner);

        assertDoesNotThrow(menu::run);

        String result = output.toString();

        assertTrue(result.contains("Invalid option."));
        assertTrue(result.contains("Program closed."));
    }

    @Test
    void shouldEnterAndReturnFromAllSubmenus() {
        Scanner scanner = new Scanner("""
                1
                0
                2
                0
                3
                0
                0
                """);

        ApplicationMenu menu = createMenu(scanner);

        assertDoesNotThrow(menu::run);

        String result = output.toString();

        assertTrue(result.contains("--- NOTES ---"));
        assertTrue(result.contains("--- TASK ---"));
        assertTrue(result.contains("--- EVENTS ---"));
        assertTrue(result.contains("Program closed."));
    }
}

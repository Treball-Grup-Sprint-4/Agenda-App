package com.agenda.common.utils;

import com.agenda.common.exception.PersistenceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleExceptionHandlerTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldHandleInvalidNumber() {
        ConsoleExceptionHandler.execute(() -> {
            throw new NumberFormatException();
        });

        assertTrue(output.toString().contains("Input must be a valid number."));
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        ConsoleExceptionHandler.execute(() -> {
            throw new IllegalArgumentException("Invalid input");
        });

        assertTrue(output.toString().contains("Invalid input"));
    }

    @Test
    void shouldHandlePersistenceException() {
        ConsoleExceptionHandler.execute(() -> {
            throw new PersistenceException("Database failure");
        });

        assertTrue(output.toString().contains("Database error. Please try again."));
    }
}

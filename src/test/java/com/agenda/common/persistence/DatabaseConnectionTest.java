package com.agenda.common.persistence;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void shouldConnectToDatabase() throws SQLException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {

            assertNotNull(connection);
            assertFalse(connection.isClosed());
            assertEquals("agenda-app-database", connection.getCatalog());
        }
    }

    @Test
    void shouldReturnSAmeInstance() {
        assertSame(DatabaseConnection.getInstance(), DatabaseConnection.getInstance());
    }

}
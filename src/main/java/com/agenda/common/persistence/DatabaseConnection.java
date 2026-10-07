package com.agenda.common.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private final static DatabaseConnection INSTANCE = new DatabaseConnection();
    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() {

        String host = System.getenv().getOrDefault("DB_HOST", "localhost");
        String port = System.getenv().getOrDefault("DB_PORT", "3306");
        String dbName = System.getenv().getOrDefault("DB_NAME", "agenda-app-database");

        this.url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
        this.user = System.getenv().getOrDefault("DB_USER", "root");
        this.password = System.getenv().getOrDefault("DB_PASSWORD", "");
    }

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}

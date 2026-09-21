package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String host = System.getenv().getOrDefault("DB_HOST", "localhost");
        String port = System.getenv().getOrDefault("DB_PORT", "3306");
        String dbName = System.getenv().getOrDefault("DB_NAME", "agenda-app-database");
        String user = System.getenv().getOrDefault("DB_USER", "root");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
        System.out.println("Connecting to " + url + " ...");

        try(Connection connection = DriverManager.getConnection(url, user, password)){
            System.out.format("Connected successfully. Catalog %s", connection.getCatalog());
        } catch(SQLException e) {
            System.err.println("Failed to connect to database");
            e.printStackTrace();
        }
    }
}

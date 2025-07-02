package com.chatapp.chatappbackend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionTester implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseConnectionTester(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            // Execute a simple query to verify DB connection
            String result = jdbcTemplate.queryForObject("SELECT 'Database connected successfully!' as message", String.class);
            System.out.println("\n====================================");
            System.out.println(result);
            System.out.println("====================================\n");
        } catch (Exception e) {
            System.err.println("\n====================================");
            System.err.println("Database connection failed!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("====================================\n");
        }
    }

    // Method to manually test the connection
    public void testConnection() {
        run();
    }
}
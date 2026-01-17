package com.gamer.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseCheckService implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Проверяем, что можем выполнить запрос к БД
            String result = jdbcTemplate.queryForObject(
                    "SELECT '✅ PostgreSQL is working!'", String.class);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("❌ PostgreSQL connection failed: " + e.getMessage());
        }
    }
}
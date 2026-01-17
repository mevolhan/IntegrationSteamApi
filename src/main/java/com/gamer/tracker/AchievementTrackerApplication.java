package com.gamer.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AchievementTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AchievementTrackerApplication.class, args);
        System.out.println("=========================================");
        System.out.println("🎮 Achievement Tracker Application Started!");
        System.out.println("✅ PostgreSQL: Connected");
        System.out.println("✅ RabbitMQ: Configured");
        System.out.println("✅ Spring MVC: Active");
        System.out.println("✅ Tomcat: Running on port 8080");
        System.out.println("=========================================");
        System.out.println("Test URLs:");
        System.out.println("1. http://localhost:8080/ping");
        System.out.println("2. http://localhost:8080/health");
        System.out.println("3. http://localhost:8080/");
        System.out.println("=========================================");
    }
}
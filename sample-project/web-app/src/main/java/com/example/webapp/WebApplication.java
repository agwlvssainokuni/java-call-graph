package com.example.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Web Application for task management.
 * Demonstrates call graph analysis with REST API and layered architecture.
 * 
 * Call hierarchy levels:
 * 1. WebApplication.main()
 * 2. Spring Boot starts → Controllers
 * 3. TaskController methods
 * 4. TaskService methods  
 * 5. TaskRepository methods
 * 6. Database/utility operations
 */
@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        System.out.println("=== Task Management Web Application ===");
        SpringApplication.run(WebApplication.class, args);
        System.out.println("Application started successfully.");
    }
}
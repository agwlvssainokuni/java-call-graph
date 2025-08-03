package com.example.webapp.controller;

import com.example.webapp.model.Task;
import com.example.webapp.service.TaskService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for task management operations.
 * This represents Level 3 in the call hierarchy (after Spring Boot startup).
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Get all tasks - Level 3 method (HTTP endpoint)
     */
    @GetMapping
    public List<Task> getAllTasks() {
        System.out.println("Controller: Getting all tasks");
        
        // Level 4: Call to service layer
        var tasks = taskService.findAllTasks();
        
        // Additional processing
        logApiAccess("GET", "/api/tasks", tasks.size());
        
        return tasks;
    }

    /**
     * Get task by ID - Level 3 method
     */
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        System.out.println("Controller: Getting task by ID: " + id);
        
        // Level 4: Service call
        var task = taskService.findTaskById(id);
        
        if (task != null) {
            logApiAccess("GET", "/api/tasks/" + id, 1);
        }
        
        return task;
    }

    /**
     * Create new task - Level 3 method
     */
    @PostMapping
    public Task createTask(@RequestBody CreateTaskRequest request) {
        System.out.println("Controller: Creating new task: " + request.title());
        
        // Level 4: Service call for creation
        var task = taskService.createTask(request.title(), request.description());
        
        // Level 4: Additional service operations
        taskService.notifyTaskCreated(task);
        
        logApiAccess("POST", "/api/tasks", 1);
        
        return task;
    }

    /**
     * Update task status - Level 3 method
     */
    @PutMapping("/{id}/status")
    public Task updateTaskStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        System.out.println("Controller: Updating task status for ID: " + id);
        
        // Level 4: Service calls
        var task = taskService.updateTaskStatus(id, request.status());
        
        if (task != null) {
            taskService.auditStatusChange(task, request.status());
        }
        
        logApiAccess("PUT", "/api/tasks/" + id + "/status", 1);
        
        return task;
    }

    /**
     * Delete task - Level 3 method
     */
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        System.out.println("Controller: Deleting task ID: " + id);
        
        // Level 4: Service call
        boolean deleted = taskService.deleteTask(id);
        
        if (deleted) {
            logApiAccess("DELETE", "/api/tasks/" + id, 1);
        }
    }

    /**
     * Get task statistics - Level 3 method
     */
    @GetMapping("/stats")
    public TaskStats getTaskStatistics() {
        System.out.println("Controller: Getting task statistics");
        
        // Level 4: Multiple service calls
        int totalTasks = taskService.getTotalTaskCount();
        int completedTasks = taskService.getCompletedTaskCount();
        int pendingTasks = taskService.getPendingTaskCount();
        
        var stats = new TaskStats(totalTasks, completedTasks, pendingTasks);
        
        logApiAccess("GET", "/api/tasks/stats", 1);
        
        return stats;
    }

    /**
     * Log API access - Level 4 method
     */
    private void logApiAccess(String method, String path, int resultCount) {
        System.out.println("Controller: API Access - " + method + " " + path + " returned " + resultCount + " results");
        
        // Level 5: More detailed logging
        recordAccessMetrics(method, path, resultCount);
    }

    /**
     * Record access metrics - Level 5 method
     */
    private void recordAccessMetrics(String method, String path, int resultCount) {
        System.out.println("Controller: Recording metrics - " + method + " " + path + " at " + System.currentTimeMillis());
    }

    // DTOs for request/response
    public record CreateTaskRequest(String title, String description) {}
    public record UpdateStatusRequest(Task.TaskStatus status) {}
    public record TaskStats(int total, int completed, int pending) {}
}
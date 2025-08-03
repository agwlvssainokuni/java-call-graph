package com.example.webapp.service;

import com.example.webapp.model.Task;
import com.example.webapp.repository.SimpleTaskRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service layer for task management operations.
 * This represents Level 4 in the call hierarchy.
 */
@Service
public class TaskService {

    private final SimpleTaskRepository taskRepository;
    private final TaskValidationService validationService;
    private final TaskNotificationService notificationService;

    public TaskService(SimpleTaskRepository taskRepository, 
                      TaskValidationService validationService,
                      TaskNotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.validationService = validationService;
        this.notificationService = notificationService;
    }

    /**
     * Find all tasks - Level 4 method called from controller
     */
    public List<Task> findAllTasks() {
        System.out.println("Service: Finding all tasks");
        
        // Level 5: Repository call
        var tasks = taskRepository.findAll();
        
        // Level 5: Additional processing
        preprocessTasks(tasks);
        
        return tasks;
    }

    /**
     * Find task by ID - Level 4 method
     */
    public Task findTaskById(Long id) {
        System.out.println("Service: Finding task by ID: " + id);
        
        // Level 5: Repository call
        var task = taskRepository.findById(id);
        
        if (task != null) {
            // Level 5: Validation
            validationService.validateTaskAccess(task);
        }
        
        return task;
    }

    /**
     * Create new task - Level 4 method
     */
    public Task createTask(String title, String description) {
        System.out.println("Service: Creating task with title: " + title);
        
        // Level 5: Validation
        validationService.validateTaskData(title, description);
        
        // Level 5: Create and save
        var task = new Task(title, description);
        var savedTask = taskRepository.save(task);
        
        // Level 5: Post-creation processing
        processNewTask(savedTask);
        
        return savedTask;
    }

    /**
     * Update task status - Level 4 method
     */
    public Task updateTaskStatus(Long id, Task.TaskStatus newStatus) {
        System.out.println("Service: Updating task status for ID: " + id);
        
        // Level 5: Repository call
        var task = taskRepository.findById(id);
        
        if (task != null) {
            // Level 5: Validation
            validationService.validateStatusTransition(task.getStatus(), newStatus);
            
            // Level 5: Update
            task.setStatus(newStatus);
            task = taskRepository.save(task);
            
            // Level 5: Additional processing
            handleStatusChange(task, newStatus);
        }
        
        return task;
    }

    /**
     * Delete task - Level 4 method
     */
    public boolean deleteTask(Long id) {
        System.out.println("Service: Deleting task ID: " + id);
        
        // Level 5: Repository operations
        var task = taskRepository.findById(id);
        
        if (task != null) {
            // Level 5: Pre-deletion validation
            validationService.validateTaskDeletion(task);
            
            // Level 5: Delete
            taskRepository.deleteById(id);
            
            // Level 5: Post-deletion cleanup
            cleanupAfterDeletion(task);
            
            return true;
        }
        
        return false;
    }

    /**
     * Get total task count - Level 4 method
     */
    public int getTotalTaskCount() {
        System.out.println("Service: Getting total task count");
        
        // Level 5: Repository call
        return taskRepository.countAll();
    }

    /**
     * Get completed task count - Level 4 method
     */
    public int getCompletedTaskCount() {
        System.out.println("Service: Getting completed task count");
        
        // Level 5: Repository call
        return taskRepository.countByStatus(Task.TaskStatus.COMPLETED);
    }

    /**
     * Get pending task count - Level 4 method  
     */
    public int getPendingTaskCount() {
        System.out.println("Service: Getting pending task count");
        
        // Level 5: Repository call
        return taskRepository.countByStatus(Task.TaskStatus.PENDING);
    }

    /**
     * Notify task created - Level 4 method
     */
    public void notifyTaskCreated(Task task) {
        System.out.println("Service: Notifying task created: " + task.getTitle());
        
        // Level 5: Notification service
        notificationService.sendTaskCreatedNotification(task);
    }

    /**
     * Audit status change - Level 4 method
     */
    public void auditStatusChange(Task task, Task.TaskStatus newStatus) {
        System.out.println("Service: Auditing status change for task: " + task.getId());
        
        // Level 5: Audit logging
        logStatusChange(task, newStatus);
        
        // Level 5: Notification
        notificationService.sendStatusChangeNotification(task, newStatus);
    }

    // Level 5 internal methods

    /**
     * Preprocess tasks - Level 5 method
     */
    private void preprocessTasks(List<Task> tasks) {
        System.out.println("Service: Preprocessing " + tasks.size() + " tasks");
        
        for (Task task : tasks) {
            // Level 6: Individual task processing
            enrichTaskData(task);
        }
    }

    /**
     * Process new task - Level 5 method
     */
    private void processNewTask(Task task) {
        System.out.println("Service: Processing new task");
        
        // Level 6: Initialize task metadata
        initializeTaskMetadata(task);
        
        // Level 6: Setup task tracking
        setupTaskTracking(task);
    }

    /**
     * Handle status change - Level 5 method
     */
    private void handleStatusChange(Task task, Task.TaskStatus newStatus) {
        System.out.println("Service: Handling status change");
        
        // Level 6: Status-specific processing
        executeStatusChangeActions(task, newStatus);
    }

    /**
     * Cleanup after deletion - Level 5 method
     */
    private void cleanupAfterDeletion(Task task) {
        System.out.println("Service: Cleaning up after task deletion");
        
        // Level 6: Remove related data
        removeTaskDependencies(task);
    }

    /**
     * Log status change - Level 5 method
     */
    private void logStatusChange(Task task, Task.TaskStatus newStatus) {
        System.out.println("Service: Logging status change");
        
        // Level 6: Detailed audit logging
        writeAuditLog(task, newStatus);
    }

    // Level 6 internal methods (deepest business logic level)

    /**
     * Enrich task data - Level 6 method
     */
    private void enrichTaskData(Task task) {
        System.out.println("Service: Enriching task data for ID: " + task.getId());
    }

    /**
     * Initialize task metadata - Level 6 method
     */
    private void initializeTaskMetadata(Task task) {
        System.out.println("Service: Initializing task metadata");
    }

    /**
     * Setup task tracking - Level 6 method
     */
    private void setupTaskTracking(Task task) {
        System.out.println("Service: Setting up task tracking");
    }

    /**
     * Execute status change actions - Level 6 method
     */
    private void executeStatusChangeActions(Task task, Task.TaskStatus newStatus) {
        System.out.println("Service: Executing status change actions");
    }

    /**
     * Remove task dependencies - Level 6 method
     */
    private void removeTaskDependencies(Task task) {
        System.out.println("Service: Removing task dependencies");
    }

    /**
     * Write audit log - Level 6 method
     */
    private void writeAuditLog(Task task, Task.TaskStatus newStatus) {
        System.out.println("Service: Writing audit log entry");
    }
}
package com.example.webapp.repository;

import com.example.webapp.model.Task;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Simple in-memory implementation of TaskRepository for call graph analysis.
 * This provides concrete method implementations that the call graph tool can analyze.
 * This represents Level 5 in the call hierarchy.
 */
@Repository
public class SimpleTaskRepository {

    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public SimpleTaskRepository() {
        // Initialize with some sample data
        initializeSampleData();
    }

    /**
     * Find task by ID - Level 5 method
     */
    public Task findById(Long id) {
        System.out.println("Repository: Finding task by ID: " + id);
        
        // Level 6: Data access operation
        var task = performFindById(id);
        
        if (task != null) {
            // Level 6: Post-retrieval processing
            enrichTaskOnRetrieval(task);
        }
        
        return task;
    }

    /**
     * Find all tasks - Level 5 method
     */
    public List<Task> findAll() {
        System.out.println("Repository: Finding all tasks");
        
        // Level 6: Data access operation
        var allTasks = performFindAll();
        
        // Level 6: Post-processing
        processTaskList(allTasks);
        
        return allTasks;
    }

    /**
     * Save task - Level 5 method
     */
    public Task save(Task task) {
        System.out.println("Repository: Saving task: " + task.getTitle());
        
        // Level 6: Pre-save processing
        preprocessTaskForSave(task);
        
        // Level 6: Actual save operation
        var savedTask = performSave(task);
        
        // Level 6: Post-save processing
        postprocessAfterSave(savedTask);
        
        return savedTask;
    }

    /**
     * Delete by ID - Level 5 method
     */
    public void deleteById(Long id) {
        System.out.println("Repository: Deleting task by ID: " + id);
        
        // Level 6: Pre-deletion processing
        var task = tasks.get(id);
        if (task != null) {
            preprocessForDeletion(task);
        }
        
        // Level 6: Actual deletion
        performDelete(id);
        
        // Level 6: Post-deletion cleanup
        cleanupAfterDeletion(id);
    }

    /**
     * Count all tasks - Level 5 method
     */
    public int countAll() {
        System.out.println("Repository: Counting all tasks");
        
        // Level 6: Count operation
        return performCountAll();
    }

    /**
     * Count tasks by status - Level 5 method
     */
    public int countByStatus(Task.TaskStatus status) {
        System.out.println("Repository: Counting tasks by status: " + status);
        
        // Level 6: Filtered count operation
        return performCountByStatus(status);
    }

    /**
     * Find tasks by status - Level 5 method
     */
    public List<Task> findByStatus(Task.TaskStatus status) {
        System.out.println("Repository: Finding tasks by status: " + status);
        
        // Level 6: Filtered search
        return performFindByStatus(status);
    }

    /**
     * Find tasks created after timestamp - Level 5 method
     */
    public List<Task> findTasksCreatedAfter(Long timestamp) {
        System.out.println("Repository: Finding tasks created after: " + timestamp);
        
        // Level 6: Time-based filtering
        return performTimeBasedSearch(timestamp);
    }

    /**
     * Find tasks by title containing - Level 5 method
     */
    public List<Task> findByTitleContaining(String keyword) {
        System.out.println("Repository: Searching tasks by keyword: " + keyword);
        
        // Level 6: Text search operation
        return performTextSearch(keyword);
    }

    // Level 6 implementation methods

    /**
     * Perform find by ID - Level 6 method
     */
    private Task performFindById(Long id) {
        System.out.println("Repository: Performing find by ID operation");
        return tasks.get(id);
    }

    /**
     * Perform find all - Level 6 method
     */
    private List<Task> performFindAll() {
        System.out.println("Repository: Performing find all operation");
        return new ArrayList<>(tasks.values());
    }

    /**
     * Perform save - Level 6 method
     */
    private Task performSave(Task task) {
        System.out.println("Repository: Performing save operation");
        
        if (task.getId() == null) {
            task.setId(idGenerator.getAndIncrement());
        }
        
        tasks.put(task.getId(), task);
        return task;
    }

    /**
     * Perform delete - Level 6 method
     */
    private void performDelete(Long id) {
        System.out.println("Repository: Performing delete operation");
        tasks.remove(id);
    }

    /**
     * Perform count all - Level 6 method
     */
    private int performCountAll() {
        System.out.println("Repository: Performing count all operation");
        return tasks.size();
    }

    /**
     * Perform count by status - Level 6 method
     */
    private int performCountByStatus(Task.TaskStatus status) {
        System.out.println("Repository: Performing count by status operation");
        
        return (int) tasks.values().stream()
                .filter(task -> task.getStatus() == status)
                .count();
    }

    /**
     * Perform find by status - Level 6 method
     */
    private List<Task> performFindByStatus(Task.TaskStatus status) {
        System.out.println("Repository: Performing find by status operation");
        
        return tasks.values().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Perform time-based search - Level 6 method
     */
    private List<Task> performTimeBasedSearch(Long timestamp) {
        System.out.println("Repository: Performing time-based search");
        
        return tasks.values().stream()
                .filter(task -> task.getCreatedAt() > timestamp)
                .collect(Collectors.toList());
    }

    /**
     * Perform text search - Level 6 method
     */
    private List<Task> performTextSearch(String keyword) {
        System.out.println("Repository: Performing text search");
        
        return tasks.values().stream()
                .filter(task -> task.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Enrich task on retrieval - Level 6 method
     */
    private void enrichTaskOnRetrieval(Task task) {
        System.out.println("Repository: Enriching task on retrieval");
        
        // Level 7: Additional data enrichment
        addMetadataToTask(task);
    }

    /**
     * Process task list - Level 6 method
     */
    private void processTaskList(List<Task> tasks) {
        System.out.println("Repository: Processing task list");
        
        // Level 7: List processing
        sortTaskList(tasks);
    }

    /**
     * Preprocess task for save - Level 6 method
     */
    private void preprocessTaskForSave(Task task) {
        System.out.println("Repository: Preprocessing task for save");
        
        // Level 7: Pre-save validation and setup
        validateTaskForSave(task);
    }

    /**
     * Postprocess after save - Level 6 method
     */
    private void postprocessAfterSave(Task task) {
        System.out.println("Repository: Postprocessing after save");
        
        // Level 7: Post-save operations
        updateTaskIndex(task);
    }

    /**
     * Preprocess for deletion - Level 6 method
     */
    private void preprocessForDeletion(Task task) {
        System.out.println("Repository: Preprocessing for deletion");
        
        // Level 7: Pre-deletion operations
        backupTaskBeforeDeletion(task);
    }

    /**
     * Cleanup after deletion - Level 6 method
     */
    private void cleanupAfterDeletion(Long id) {
        System.out.println("Repository: Cleaning up after deletion");
        
        // Level 7: Post-deletion cleanup
        removeFromIndexes(id);
    }

    /**
     * Initialize sample data - Level 6 method
     */
    private void initializeSampleData() {
        System.out.println("Repository: Initializing sample data");
        
        // Level 7: Data setup
        createSampleTasks();
    }

    // Level 7 utility methods (deepest data access level)

    /**
     * Add metadata to task - Level 7 method
     */
    private void addMetadataToTask(Task task) {
        System.out.println("Repository: Adding metadata to task");
    }

    /**
     * Sort task list - Level 7 method
     */
    private void sortTaskList(List<Task> tasks) {
        System.out.println("Repository: Sorting task list");
        tasks.sort(Comparator.comparing(Task::getCreatedAt));
    }

    /**
     * Validate task for save - Level 7 method
     */
    private void validateTaskForSave(Task task) {
        System.out.println("Repository: Validating task for save");
    }

    /**
     * Update task index - Level 7 method
     */
    private void updateTaskIndex(Task task) {
        System.out.println("Repository: Updating task index");
    }

    /**
     * Backup task before deletion - Level 7 method
     */
    private void backupTaskBeforeDeletion(Task task) {
        System.out.println("Repository: Backing up task before deletion");
    }

    /**
     * Remove from indexes - Level 7 method
     */
    private void removeFromIndexes(Long id) {
        System.out.println("Repository: Removing from indexes");
    }

    /**
     * Create sample tasks - Level 7 method
     */
    private void createSampleTasks() {
        System.out.println("Repository: Creating sample tasks");
        
        save(new Task("Setup Development Environment", "Install and configure development tools"));
        save(new Task("Design Database Schema", "Create entity relationship diagram"));
        save(new Task("Implement REST API", "Create controllers and services"));
    }
}
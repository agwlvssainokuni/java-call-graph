package com.example.webapp.service;

import com.example.webapp.model.Task;
import org.springframework.stereotype.Service;

/**
 * Service for task validation operations.
 * This represents Level 5 in the call hierarchy.
 */
@Service
public class TaskValidationService {

    /**
     * Validate task data - Level 5 method
     */
    public void validateTaskData(String title, String description) {
        System.out.println("Validation: Validating task data");
        
        // Level 6: Individual validations
        validateTitle(title);
        validateDescription(description);
        
        // Level 6: Business rules validation
        validateBusinessRules(title, description);
    }

    /**
     * Validate task access - Level 5 method
     */
    public void validateTaskAccess(Task task) {
        System.out.println("Validation: Validating task access for ID: " + task.getId());
        
        // Level 6: Access permissions
        checkAccessPermissions(task);
        
        // Level 6: Task state validation
        validateTaskState(task);
    }

    /**
     * Validate status transition - Level 5 method
     */
    public void validateStatusTransition(Task.TaskStatus currentStatus, Task.TaskStatus newStatus) {
        System.out.println("Validation: Validating status transition from " + currentStatus + " to " + newStatus);
        
        // Level 6: Transition rules
        checkTransitionRules(currentStatus, newStatus);
        
        // Level 6: Business constraints
        validateStatusConstraints(newStatus);
    }

    /**
     * Validate task deletion - Level 5 method
     */
    public void validateTaskDeletion(Task task) {
        System.out.println("Validation: Validating task deletion for ID: " + task.getId());
        
        // Level 6: Deletion constraints
        checkDeletionConstraints(task);
        
        // Level 6: Dependencies check
        validateNoDependencies(task);
    }

    // Level 6 validation methods

    /**
     * Validate title - Level 6 method
     */
    private void validateTitle(String title) {
        System.out.println("Validation: Checking title validity");
        
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        
        if (title.length() > 100) {
            throw new IllegalArgumentException("Task title too long");
        }
    }

    /**
     * Validate description - Level 6 method
     */
    private void validateDescription(String description) {
        System.out.println("Validation: Checking description validity");
        
        if (description != null && description.length() > 1000) {
            throw new IllegalArgumentException("Task description too long");
        }
    }

    /**
     * Validate business rules - Level 6 method
     */
    private void validateBusinessRules(String title, String description) {
        System.out.println("Validation: Checking business rules");
        
        // Level 7: Specific business rule checks
        checkTitleUniqueness(title);
        checkContentPolicy(title, description);
    }

    /**
     * Check access permissions - Level 6 method
     */
    private void checkAccessPermissions(Task task) {
        System.out.println("Validation: Checking access permissions");
        
        // Level 7: Permission validation
        validateUserAccess(task);
    }

    /**
     * Validate task state - Level 6 method
     */
    private void validateTaskState(Task task) {
        System.out.println("Validation: Validating task state");
        
        // Level 7: State consistency checks
        checkStateConsistency(task);
    }

    /**
     * Check transition rules - Level 6 method
     */
    private void checkTransitionRules(Task.TaskStatus currentStatus, Task.TaskStatus newStatus) {
        System.out.println("Validation: Checking transition rules");
        
        // Level 7: Specific transition validations
        validateAllowedTransition(currentStatus, newStatus);
    }

    /**
     * Validate status constraints - Level 6 method
     */
    private void validateStatusConstraints(Task.TaskStatus status) {
        System.out.println("Validation: Validating status constraints");
        
        // Level 7: Status-specific constraints
        checkStatusSpecificRules(status);
    }

    /**
     * Check deletion constraints - Level 6 method
     */
    private void checkDeletionConstraints(Task task) {
        System.out.println("Validation: Checking deletion constraints");
        
        // Level 7: Constraint validation
        validateDeletionRules(task);
    }

    /**
     * Validate no dependencies - Level 6 method
     */
    private void validateNoDependencies(Task task) {
        System.out.println("Validation: Checking for task dependencies");
        
        // Level 7: Dependency checks
        checkForBlockingDependencies(task);
    }

    // Level 7 validation methods (deepest validation level)

    /**
     * Check title uniqueness - Level 7 method
     */
    private void checkTitleUniqueness(String title) {
        System.out.println("Validation: Checking title uniqueness");
        // Simulate uniqueness check
    }

    /**
     * Check content policy - Level 7 method
     */
    private void checkContentPolicy(String title, String description) {
        System.out.println("Validation: Checking content policy compliance");
        // Simulate content policy validation
    }

    /**
     * Validate user access - Level 7 method
     */
    private void validateUserAccess(Task task) {
        System.out.println("Validation: Validating user access rights");
        // Simulate user access validation
    }

    /**
     * Check state consistency - Level 7 method
     */
    private void checkStateConsistency(Task task) {
        System.out.println("Validation: Checking task state consistency");
        // Simulate state consistency checks
    }

    /**
     * Validate allowed transition - Level 7 method
     */
    private void validateAllowedTransition(Task.TaskStatus from, Task.TaskStatus to) {
        System.out.println("Validation: Validating allowed transition");
        // Simulate transition validation logic
    }

    /**
     * Check status specific rules - Level 7 method
     */
    private void checkStatusSpecificRules(Task.TaskStatus status) {
        System.out.println("Validation: Checking status-specific rules");
        // Simulate status-specific validation
    }

    /**
     * Validate deletion rules - Level 7 method
     */
    private void validateDeletionRules(Task task) {
        System.out.println("Validation: Validating deletion rules");
        // Simulate deletion rule validation
    }

    /**
     * Check for blocking dependencies - Level 7 method
     */
    private void checkForBlockingDependencies(Task task) {
        System.out.println("Validation: Checking for blocking dependencies");
        // Simulate dependency validation
    }
}
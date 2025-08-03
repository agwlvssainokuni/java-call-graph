package com.example.webapp.service;

import com.example.webapp.model.Task;
import org.springframework.stereotype.Service;

/**
 * Service for task notification operations.
 * This represents Level 5 in the call hierarchy.
 */
@Service
public class TaskNotificationService {

    /**
     * Send task created notification - Level 5 method
     */
    public void sendTaskCreatedNotification(Task task) {
        System.out.println("Notification: Sending task created notification for: " + task.getTitle());
        
        // Level 6: Prepare notification
        var notification = prepareCreatedNotification(task);
        
        // Level 6: Send notification
        sendNotification(notification);
        
        // Level 6: Log notification
        logNotificationSent("TASK_CREATED", task.getId());
    }

    /**
     * Send status change notification - Level 5 method
     */
    public void sendStatusChangeNotification(Task task, Task.TaskStatus newStatus) {
        System.out.println("Notification: Sending status change notification");
        
        // Level 6: Prepare notification
        var notification = prepareStatusChangeNotification(task, newStatus);
        
        // Level 6: Send notification
        sendNotification(notification);
        
        // Level 6: Log notification
        logNotificationSent("STATUS_CHANGE", task.getId());
    }

    /**
     * Prepare created notification - Level 6 method
     */
    private TaskNotification prepareCreatedNotification(Task task) {
        System.out.println("Notification: Preparing created notification");
        
        // Level 7: Build notification content
        String subject = buildCreatedSubject(task);
        String body = buildCreatedBody(task);
        
        return new TaskNotification(subject, body, task.getId());
    }

    /**
     * Prepare status change notification - Level 6 method
     */
    private TaskNotification prepareStatusChangeNotification(Task task, Task.TaskStatus newStatus) {
        System.out.println("Notification: Preparing status change notification");
        
        // Level 7: Build notification content
        String subject = buildStatusChangeSubject(task, newStatus);
        String body = buildStatusChangeBody(task, newStatus);
        
        return new TaskNotification(subject, body, task.getId());
    }

    /**
     * Send notification - Level 6 method
     */
    private void sendNotification(TaskNotification notification) {
        System.out.println("Notification: Sending notification - " + notification.subject());
        
        // Level 7: Actual sending logic
        deliverNotification(notification);
    }

    /**
     * Log notification sent - Level 6 method
     */
    private void logNotificationSent(String type, Long taskId) {
        System.out.println("Notification: Logging notification sent");
        
        // Level 7: Write to notification log
        writeNotificationLog(type, taskId, System.currentTimeMillis());
    }

    // Level 7 notification methods (deepest level)

    /**
     * Build created subject - Level 7 method
     */
    private String buildCreatedSubject(Task task) {
        System.out.println("Notification: Building created notification subject");
        return "New Task Created: " + task.getTitle();
    }

    /**
     * Build created body - Level 7 method
     */
    private String buildCreatedBody(Task task) {
        System.out.println("Notification: Building created notification body");
        return "A new task has been created: " + task.getTitle() + "\nDescription: " + task.getDescription();
    }

    /**
     * Build status change subject - Level 7 method
     */
    private String buildStatusChangeSubject(Task task, Task.TaskStatus newStatus) {
        System.out.println("Notification: Building status change subject");
        return "Task Status Changed: " + task.getTitle() + " -> " + newStatus;
    }

    /**
     * Build status change body - Level 7 method
     */
    private String buildStatusChangeBody(Task task, Task.TaskStatus newStatus) {
        System.out.println("Notification: Building status change body");
        return "Task '" + task.getTitle() + "' status changed to: " + newStatus;
    }

    /**
     * Deliver notification - Level 7 method
     */
    private void deliverNotification(TaskNotification notification) {
        System.out.println("Notification: Delivering notification via system");
        // Simulate notification delivery (email, SMS, push, etc.)
    }

    /**
     * Write notification log - Level 7 method
     */
    private void writeNotificationLog(String type, Long taskId, long timestamp) {
        System.out.println("Notification: Writing to notification log");
        // Simulate logging to notification audit trail
    }

    // Notification data record
    private record TaskNotification(String subject, String body, Long taskId) {}
}
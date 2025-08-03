package com.example.cliapp.service;

/**
 * File validation service - Level 5 in call hierarchy
 */
public class FileValidationService {

    /**
     * Validate file structure - Level 5 method
     */
    public void validateFileStructure(FileProcessingService.FileInfo fileInfo) {
        System.out.println("Validation: Checking file structure for " + fileInfo.name());
        
        // Level 6: Internal validation logic
        checkFileExtension(fileInfo.name());
        checkFileSize(fileInfo.size());
    }

    /**
     * Validate file permissions - Level 5 method
     */
    public void validateFilePermissions(String filePath) {
        System.out.println("Validation: Checking file permissions for " + filePath);
        
        // Level 6: Permission checks
        checkReadPermission(filePath);
        checkWritePermission(filePath);
    }

    /**
     * Check file extension - Level 6 method
     */
    private void checkFileExtension(String fileName) {
        System.out.println("Validation: Checking file extension");
        
        if (!fileName.contains(".")) {
            System.out.println("Warning: File has no extension");
        }
    }

    /**
     * Check file size - Level 6 method
     */
    private void checkFileSize(long size) {
        System.out.println("Validation: Checking file size");
        
        if (size > 10_000_000) { // 10MB
            System.out.println("Warning: Large file detected");
        }
    }

    /**
     * Check read permission - Level 6 method
     */
    private void checkReadPermission(String filePath) {
        System.out.println("Validation: Checking read permission");
        // Simulate permission check
    }

    /**
     * Check write permission - Level 6 method
     */
    private void checkWritePermission(String filePath) {
        System.out.println("Validation: Checking write permission");
        // Simulate permission check
    }
}
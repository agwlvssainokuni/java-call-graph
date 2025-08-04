package com.example.cliapp.cli;

import com.example.cliapp.service.FileProcessingService;

import java.util.List;

/**
 * Command Line Interface handler for file processing operations.
 * This represents Level 3 in the call hierarchy.
 */
public class FileProcessorCli {

    private FileProcessingService fileProcessingService;

    /**
     * Constructor - initialize dependencies manually
     */
    public FileProcessorCli() {
        this.fileProcessingService = new FileProcessingService();
    }

    /**
     * Process a single file - Level 3 method called from SimpleCliApplication
     */
    public void processFile(String filePath) {
        System.out.println("CLI: Processing file: " + filePath);

        try {
            // Level 4: Call to service layer
            var result = fileProcessingService.analyzeFile(filePath);
            System.out.println("CLI: File analysis result - " + result.summary());

            // Another service call
            fileProcessingService.validateFile(filePath);

        } catch (Exception e) {
            System.err.println("CLI: Failed to process file " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Print processing summary - calls service for statistics
     */
    private void printProcessingSummary(int fileCount) {
        System.out.println("CLI: Generating processing summary...");

        // Level 4: Another service call
        var stats = fileProcessingService.getProcessingStatistics();
        System.out.println("CLI: Processed " + fileCount + " files. " + stats);
    }
}

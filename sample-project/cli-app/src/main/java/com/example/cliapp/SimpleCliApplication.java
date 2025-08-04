package com.example.cliapp;

import com.example.cliapp.cli.FileProcessorCli;

/**
 * Simple CLI Application without Spring Boot for clear call graph analysis.
 * This version uses direct instantiation instead of dependency injection.
 * <p>
 * Call hierarchy levels:
 * 1. SimpleCliApplication.main()
 * 2. SimpleCliApplication.processFiles()
 * 3. FileProcessorCli.processFiles()
 * 4. FileProcessingService methods
 * 5. FileRepository methods
 * 6. FileSystemUtil methods
 */
public class SimpleCliApplication {

    public static void main(String[] args) {
        System.out.println("=== Simple File Processing CLI Application ===");

        if (args.length == 0) {
            System.out.println("Usage: java SimpleCliApplication <file-path> [<file-path>...]");
            System.out.println("Processes files and demonstrates call graph analysis.");
            System.exit(1);
        }

        // Level 2: Call to instance method
        SimpleCliApplication app = new SimpleCliApplication();
        app.processFiles(args);
    }

    /**
     * Process files - Level 2 method called from main
     */
    public void processFiles(String[] filePaths) {
        System.out.println("App: Starting file processing for " + filePaths.length + " files");

        try {
            // Level 3: Call to CLI handler (direct instantiation, no DI)
            FileProcessorCli cli = new FileProcessorCli();

            for (String filePath : filePaths) {
                cli.processFile(filePath);
            }

            // Another method call
            printSummary(filePaths.length);

            System.out.println("App: File processing completed successfully.");
        } catch (Exception e) {
            System.err.println("App: Error processing files: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Print summary - Level 2 method
     */
    private void printSummary(int fileCount) {
        System.out.println("App: Processed " + fileCount + " files in total.");

        // Level 3: Another call
        logCompletion();
    }

    /**
     * Log completion - Level 3 method
     */
    private void logCompletion() {
        System.out.println("App: Processing completed at " + System.currentTimeMillis());
    }
}

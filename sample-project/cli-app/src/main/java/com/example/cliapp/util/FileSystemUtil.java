package com.example.cliapp.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * File system utility operations.
 * This represents Level 6-7 in the call hierarchy - the deepest level.
 */
public class FileSystemUtil {

    /**
     * Check if file exists - Level 6 method
     */
    public boolean fileExists(String filePath) {
        System.out.println("FileUtil: Checking if file exists: " + filePath);

        // Level 7: Low-level file system operation
        return performFileExistsCheck(filePath);
    }

    /**
     * Get file size - Level 6 method
     */
    public long getFileSize(String filePath) {
        System.out.println("FileUtil: Getting file size for " + filePath);

        try {
            // Level 7: File system call
            return Files.size(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Error getting file size: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get file name - Level 6 method
     */
    public String getFileName(String filePath) {
        System.out.println("FileUtil: Extracting file name from " + filePath);

        // Level 7: String manipulation
        return extractFileName(filePath);
    }

    /**
     * Read file as string - Level 6 method
     */
    public String readFileAsString(String filePath) {
        System.out.println("FileUtil: Reading file content from " + filePath);

        try {
            // Level 7: File system read operation
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return "Error reading file: " + filePath;
        }
    }

    /**
     * Write string to file - Level 6 method
     */
    public void writeStringToFile(String filePath, String content) {
        System.out.println("FileUtil: Writing content to " + filePath);

        try {
            // Level 7: Ensure directory exists
            ensureDirectoryExists(filePath);

            // Level 7: File system write operation
            Files.writeString(Paths.get(filePath), content);
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }

    /**
     * Append to log file - Level 7 method
     */
    public void appendToLogFile(String logEntry) {
        System.out.println("FileUtil: Appending to log file");

        try {
            String logPath = "processing.log";

            // Level 8: File system append operation
            Files.writeString(Paths.get(logPath), logEntry + "\n",
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error writing to log: " + e.getMessage());
        }
    }

    /**
     * Get result path - Level 6 method
     */
    public String getResultPath(String originalPath) {
        System.out.println("FileUtil: Generating result path for " + originalPath);

        // Level 7: Path manipulation
        return buildResultPath(originalPath);
    }

    /**
     * Get metadata path - Level 7 method
     */
    public String getMetadataPath(String originalPath) {
        System.out.println("FileUtil: Generating metadata path for " + originalPath);

        // Level 8: Path manipulation
        return buildMetadataPath(originalPath);
    }

    // Level 7 internal methods

    /**
     * Perform file exists check - Level 7 method
     */
    private boolean performFileExistsCheck(String filePath) {
        System.out.println("FileUtil: Performing low-level file existence check");

        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * Extract file name - Level 7 method
     */
    private String extractFileName(String filePath) {
        System.out.println("FileUtil: Extracting file name");

        Path path = Paths.get(filePath);
        return path.getFileName().toString();
    }

    /**
     * Ensure directory exists - Level 7 method
     */
    private void ensureDirectoryExists(String filePath) throws IOException {
        System.out.println("FileUtil: Ensuring directory exists");

        Path path = Paths.get(filePath);
        Path parentDir = path.getParent();

        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Build result path - Level 7 method
     */
    private String buildResultPath(String originalPath) {
        System.out.println("FileUtil: Building result path");

        return originalPath + ".analysis";
    }

    /**
     * Build metadata path - Level 8 method (deepest level)
     */
    private String buildMetadataPath(String originalPath) {
        System.out.println("FileUtil: Building metadata path (deepest level)");

        return originalPath + ".metadata";
    }
}

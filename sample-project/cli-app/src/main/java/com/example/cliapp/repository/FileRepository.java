package com.example.cliapp.repository;

import com.example.cliapp.service.FileProcessingService.FileInfo;
import com.example.cliapp.service.FileProcessingService.FileAnalysisResult;
import com.example.cliapp.util.FileSystemUtil;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repository layer for file operations.
 * This represents Level 5 in the call hierarchy.
 */
public class FileRepository {

    private FileSystemUtil fileSystemUtil;

    // Simulate processing statistics
    private final AtomicInteger totalFiles = new AtomicInteger(0);
    private final AtomicLong totalBytes = new AtomicLong(0);

    /**
     * Constructor - initialize dependencies manually
     */
    public FileRepository() {
        this.fileSystemUtil = new FileSystemUtil();
    }

    /**
     * Get file information - Level 5 method called from service
     */
    public FileInfo getFileInfo(String filePath) {
        System.out.println("Repository: Getting file info for " + filePath);

        // Level 6: Call to utility layer
        if (!fileSystemUtil.fileExists(filePath)) {
            return null;
        }

        // Level 6: More utility calls
        long size = fileSystemUtil.getFileSize(filePath);
        String name = fileSystemUtil.getFileName(filePath);

        totalFiles.incrementAndGet();
        totalBytes.addAndGet(size);

        return new FileInfo(name, size, filePath);
    }

    /**
     * Read file content - Level 5 method
     */
    public String readFileContent(String filePath) {
        System.out.println("Repository: Reading file content from " + filePath);

        // Level 6: Utility call for file reading
        var content = fileSystemUtil.readFileAsString(filePath);

        // Level 6: Additional utility operations
        logFileAccess(filePath);

        return content;
    }

    /**
     * Save analysis result - Level 5 method
     */
    public void saveAnalysisResult(String filePath, FileAnalysisResult result) {
        System.out.println("Repository: Saving analysis result for " + filePath);

        // Level 6: Utility calls for saving
        String resultPath = fileSystemUtil.getResultPath(filePath);
        fileSystemUtil.writeStringToFile(resultPath, formatAnalysisResult(result));

        // Level 6: Update metadata
        updateFileMetadata(filePath, result);
    }

    /**
     * Get total files processed - Level 5 method
     */
    public int getTotalFilesProcessed() {
        System.out.println("Repository: Getting total files processed");
        return totalFiles.get();
    }

    /**
     * Get total bytes processed - Level 5 method
     */
    public long getTotalBytesProcessed() {
        System.out.println("Repository: Getting total bytes processed");
        return totalBytes.get();
    }

    /**
     * Log file access - Level 6 method
     */
    private void logFileAccess(String filePath) {
        System.out.println("Repository: Logging file access");

        // Level 7: Utility call for logging
        fileSystemUtil.appendToLogFile("ACCESS: " + filePath + " at " + System.currentTimeMillis());
    }

    /**
     * Format analysis result - Level 6 method
     */
    private String formatAnalysisResult(FileAnalysisResult result) {
        System.out.println("Repository: Formatting analysis result");

        return String.format("Analysis Result:\nFile: %s\nSize: %d\nLines: %d\nWords: %d\nChecksum: %s\n",
                result.fileName(), result.size(), result.lineCount(), result.wordCount(), result.checksum());
    }

    /**
     * Update file metadata - Level 6 method
     */
    private void updateFileMetadata(String filePath, FileAnalysisResult result) {
        System.out.println("Repository: Updating file metadata");

        // Level 7: Utility calls for metadata
        String metadataPath = fileSystemUtil.getMetadataPath(filePath);
        String metadata = createMetadata(result);
        fileSystemUtil.writeStringToFile(metadataPath, metadata);
    }

    /**
     * Create metadata - Level 7 method
     */
    private String createMetadata(FileAnalysisResult result) {
        System.out.println("Repository: Creating metadata");

        return "METADATA:" + result.fileName() + ":" + result.checksum();
    }
}

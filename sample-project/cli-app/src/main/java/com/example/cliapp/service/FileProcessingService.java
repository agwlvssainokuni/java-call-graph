package com.example.cliapp.service;

import com.example.cliapp.repository.FileRepository;

/**
 * Service layer for file processing operations.
 * This represents Level 4 in the call hierarchy.
 */
public class FileProcessingService {

    private FileRepository fileRepository;
    private FileValidationService validationService;

    /**
     * Constructor - initialize dependencies manually
     */
    public FileProcessingService() {
        this.fileRepository = new FileRepository();
        this.validationService = new FileValidationService();
    }

    /**
     * Analyze file content - Level 4 method called from FileProcessorCli
     */
    public FileAnalysisResult analyzeFile(String filePath) {
        System.out.println("Service: Analyzing file " + filePath);
        
        // Level 5: Call to repository layer
        var fileInfo = fileRepository.getFileInfo(filePath);
        
        if (fileInfo == null) {
            throw new RuntimeException("File not found: " + filePath);
        }
        
        // Level 5: Another repository call
        var content = fileRepository.readFileContent(filePath);
        
        // Process the content
        var analysisResult = performAnalysis(content, fileInfo);
        
        // Level 5: Store analysis result
        fileRepository.saveAnalysisResult(filePath, analysisResult);
        
        return analysisResult;
    }

    /**
     * Validate file - another Level 4 method
     */
    public void validateFile(String filePath) {
        System.out.println("Service: Validating file " + filePath);
        
        // Level 5: Repository call for validation
        var fileInfo = fileRepository.getFileInfo(filePath);
        
        if (fileInfo != null) {
            // Level 5: Call to validation service
            validationService.validateFileStructure(fileInfo);
            validationService.validateFilePermissions(filePath);
        }
    }

    /**
     * Get processing statistics - Level 4 method
     */
    public String getProcessingStatistics() {
        System.out.println("Service: Collecting processing statistics");
        
        // Level 5: Repository calls
        int totalFiles = fileRepository.getTotalFilesProcessed();
        long totalSize = fileRepository.getTotalBytesProcessed();
        
        return String.format("Total files: %d, Total size: %d bytes", totalFiles, totalSize);
    }

    /**
     * Internal analysis logic - Level 5 method
     */
    private FileAnalysisResult performAnalysis(String content, FileInfo fileInfo) {
        System.out.println("Service: Performing internal analysis");
        
        // Level 6: Call to utility methods
        var metrics = calculateMetrics(content);
        var checksum = generateChecksum(content);
        
        return new FileAnalysisResult(
            fileInfo.name(),
            content.length(),
            metrics.lineCount(),
            metrics.wordCount(),
            checksum
        );
    }

    /**
     * Calculate file metrics - Level 6 method
     */
    private FileMetrics calculateMetrics(String content) {
        System.out.println("Service: Calculating file metrics");
        
        int lineCount = content.split("\n").length;
        int wordCount = content.split("\\s+").length;
        
        return new FileMetrics(lineCount, wordCount);
    }

    /**
     * Generate file checksum - Level 6 method  
     */
    private String generateChecksum(String content) {
        System.out.println("Service: Generating checksum");
        return "checksum_" + content.hashCode();
    }

    // Data records for analysis results
    public record FileAnalysisResult(String fileName, int size, int lineCount, int wordCount, String checksum) {
        public String summary() {
            return String.format("File: %s, Size: %d, Lines: %d, Words: %d", fileName, size, lineCount, wordCount);
        }
    }
    
    public record FileMetrics(int lineCount, int wordCount) {}
    
    public record FileInfo(String name, long size, String path) {}
}
package com.example.cliapp;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ExitCodeGenerator;
import com.example.cliapp.cli.FileProcessorCli;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Spring Boot CLI Application for file processing.
 * Demonstrates call graph analysis with layered architecture.
 * 
 * Call hierarchy levels:
 * 1. CliApplication.main()
 * 2. CliApplication.run() 
 * 3. FileProcessorCli.processFiles()
 * 4. FileProcessingService methods
 * 5. FileRepository methods
 * 6. FileSystemUtil methods
 */
@SpringBootApplication
public class CliApplication implements ApplicationRunner, ExitCodeGenerator {

    private int exitCode = 0;

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(CliApplication.class, args)));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("=== File Processing CLI Application ===");
        
        if (args.getNonOptionArgs().isEmpty()) {
            System.out.println("Usage: java -jar cli-app.jar <file-path> [<file-path>...]");
            System.out.println("Processes files and demonstrates call graph analysis.");
            exitCode = 1;
            return;
        }
        
        try {
            // Level 2: Call to CLI handler
            FileProcessorCli fileProcessorCli = new FileProcessorCli();
            
            for (String filePath : args.getNonOptionArgs()) {
                fileProcessorCli.processFile(filePath);
            }
            
            System.out.println("File processing completed successfully.");
        } catch (Exception e) {
            System.err.println("Error processing files: " + e.getMessage());
            exitCode = 1;
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
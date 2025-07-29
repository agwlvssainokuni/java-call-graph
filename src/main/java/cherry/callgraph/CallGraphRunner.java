/*
 * Copyright 2025 agwlvssainokuni
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cherry.callgraph;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class CallGraphRunner implements ApplicationRunner, ExitCodeGenerator {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SootUpAnalyzer sootUpAnalyzer;
    private final OutputFormatter outputFormatter;
    private int exitCode = 0;

    public CallGraphRunner(SootUpAnalyzer sootUpAnalyzer, OutputFormatter outputFormatter) {
        this.sootUpAnalyzer = sootUpAnalyzer;
        this.outputFormatter = outputFormatter;
    }

    @Override
    public void run(@Nonnull ApplicationArguments args) {
        if (args.getNonOptionArgs().isEmpty()) {
            if (!args.containsOption("quiet")) {
                printUsage();
            }
            exitCode = 0;
            return;
        }

        try {
            processFiles(args);
            exitCode = 0;
        } catch (Exception e) {
            if (!args.containsOption("quiet")) {
                logger.error("Error processing files: {}", e.getMessage());
                if (logger.isDebugEnabled()) {
                    logger.debug("Stack trace:", e);
                }
            }
            exitCode = 1;
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    private void printUsage() {
        logger.info("Usage: java -jar java-call-graph.jar [options] <file|directory>...");
        logger.info("Options:");
        logger.info("  --output=<file>        Output file for call graph (default: stdout)");
        logger.info("  --format=<format>      Output format: txt, csv, dot (default: txt)");
        logger.info("  --algorithm=<algo>     Algorithm: cha, rta (default: cha)");
        logger.info("  --entry=<method>       Entry point method (default: main methods)");
        logger.info("  --package=<package>    Filter by package name");
        logger.info("  --exclude-jdk          Exclude JDK classes from analysis");
        logger.info("  --quiet                Suppress standard output");
        logger.info("  --verbose              Show detailed information");
        logger.info("  --help                 Show this help message");
    }

    private void processFiles(@Nonnull ApplicationArguments args) {
        var files = validateInputFiles(args.getNonOptionArgs());

        if (files.isEmpty()) {
            throw new IllegalArgumentException("No valid input files or directories found");
        }

        var quiet = args.containsOption("quiet");
        var verbose = args.containsOption("verbose");

        if (!quiet) {
            logger.info("Processing {} input(s):", files.size());
            for (String file : files) {
                logger.info("  {}", file);
            }
        }

        // Parse output options
        var outputFile = getOptionValue(args, "output", null);
        var formatStr = getOptionValue(args, "format", "txt");
        var format = parseOutputFormat(formatStr);

        // Parse package filters
        var packageFilters = parsePackageFilters(args);
        if (!quiet && !packageFilters.isEmpty()) {
            logger.info("Package filters: {}", String.join(", ", packageFilters));
        }

        // Parse algorithm
        var algorithmStr = getOptionValue(args, "algorithm", "cha");
        var algorithm = parseAlgorithm(algorithmStr);
        if (!quiet) {
            logger.info("Analysis algorithm: {}", algorithm);
        }

        // Parse custom entry points
        var customEntryPoints = parseEntryPoints(args);
        if (!quiet && !customEntryPoints.isEmpty()) {
            logger.info("Custom entry points: {}", String.join(", ", customEntryPoints));
        }

        // Parse JDK exclusion option
        var excludeJdk = args.containsOption("exclude-jdk");
        if (!quiet && excludeJdk) {
            logger.info("JDK classes will be excluded from analysis");
        }

        // Perform SootUp analysis
        try {
            var result = sootUpAnalyzer.analyzeFiles(files, verbose, packageFilters, algorithm, customEntryPoints, excludeJdk);

            // Write output in specified format
            if (outputFile != null || format != OutputFormatter.Format.TXT) {
                outputFormatter.writeOutput(result, format, outputFile, verbose);
                if (!quiet && outputFile != null) {
                    logger.info("Output written to: {}", outputFile);
                }
            } else if (!quiet) {
                displayResults(result, verbose);
            }

        } catch (Exception e) {
            if (!quiet) {
                logger.error("SootUp analysis failed: {}", e.getMessage());
                logger.error("Exception type: {}", e.getClass().getSimpleName());
                if (verbose) {
                    logger.error("Stack trace:", e);
                }
            }
            throw new RuntimeException("SootUp analysis failed", e);
        }
    }

    @Nonnull
    private List<String> validateInputFiles(@Nonnull List<String> inputArgs) {
        return inputArgs.stream()
                .filter(this::isValidInputFile)
                .toList();
    }

    private boolean isValidInputFile(@Nonnull String filePath) {
        try {
            Path path = Paths.get(filePath);

            if (!Files.exists(path)) {
                logger.warn("File or directory does not exist: {}", filePath);
                return false;
            }

            if (Files.isDirectory(path)) {
                return true;
            }

            if (Files.isRegularFile(path)) {
                String fileName = path.getFileName().toString().toLowerCase();
                if (fileName.endsWith(".jar") || fileName.endsWith(".class") || fileName.endsWith(".war")) {
                    return true;
                } else {
                    logger.warn("Unsupported file type: {} (supported: .jar, .class, .war)", filePath);
                    return false;
                }
            }

            logger.warn("Not a regular file or directory: {}", filePath);
            return false;

        } catch (Exception e) {
            logger.warn("Error validating file: {} - {}", filePath, e.getMessage());
            return false;
        }
    }

    private void displayResults(@Nonnull SootUpAnalyzer.AnalysisResult result, boolean verbose) {
        logger.info("");
        logger.info("=== Call Graph Analysis Results ===");

        // Always show call graph edges
        logger.info("Call Graph ({} edges):", result.callEdges().size());
        for (var callEdge : result.callEdges()) {
            logger.info("  {}.{} -> {}.{}",
                    callEdge.sourceClass(),
                    callEdge.sourceMethod(),
                    callEdge.targetClass(),
                    callEdge.targetMethod()
            );
        }

        if (verbose) {
            logger.info("");
            logger.info("Classes found:");
            for (var classInfo : result.classes()) {
                String type = classInfo.isInterface() ? "interface" :
                        classInfo.isAbstract() ? "abstract class" : "class";
                logger.info("  {} ({})", classInfo.name(), type);
            }

            logger.info("");
            logger.info("Methods found:");
            for (var methodInfo : result.methods()) {
                String visibility = methodInfo.isPublic() ? "public" :
                        methodInfo.isPrivate() ? "private" : "package";
                String modifier = methodInfo.isStatic() ? "static" : "instance";
                logger.info("  {}.{} ({} {})",
                        methodInfo.className(),
                        methodInfo.methodName(),
                        visibility,
                        modifier
                );
            }
        } else {
            logger.info("");
            logger.info("Classes ({}):", result.classes().size());
            for (var classInfo : result.classes()) {
                logger.info("  {}", classInfo.name());
            }
        }
    }

    private String getOptionValue(@Nonnull ApplicationArguments args, @Nonnull String option, String defaultValue) {
        var values = args.getOptionValues(option);
        return values != null && !values.isEmpty() ? values.getFirst() : defaultValue;
    }

    private OutputFormatter.Format parseOutputFormat(@Nonnull String formatStr) {
        return switch (formatStr.toLowerCase()) {
            case "txt", "text" -> OutputFormatter.Format.TXT;
            case "csv" -> OutputFormatter.Format.CSV;
            case "dot", "graphviz" -> OutputFormatter.Format.DOT;
            default -> {
                logger.warn("Unknown output format '{}', using TXT format", formatStr);
                yield OutputFormatter.Format.TXT;
            }
        };
    }

    @Nonnull
    private List<String> parsePackageFilters(@Nonnull ApplicationArguments args) {
        var packageOptions = args.getOptionValues("package");
        if (packageOptions == null || packageOptions.isEmpty()) {
            return List.of();
        }

        List<String> filters = new ArrayList<>();
        for (String packageOption : packageOptions) {
            // Split by comma to support multiple packages in one option
            String[] packages = packageOption.split(",");
            for (String pkg : packages) {
                String trimmed = pkg.trim();
                if (!trimmed.isEmpty()) {
                    filters.add(trimmed);
                }
            }
        }

        return filters;
    }

    @Nonnull
    private SootUpAnalyzer.Algorithm parseAlgorithm(@Nonnull String algorithmStr) {
        return switch (algorithmStr.toLowerCase()) {
            case "cha" -> SootUpAnalyzer.Algorithm.CHA;
            case "rta" -> SootUpAnalyzer.Algorithm.RTA;
            default -> {
                logger.warn("Unknown algorithm '{}', using CHA. Supported: cha, rta", algorithmStr);
                yield SootUpAnalyzer.Algorithm.CHA;
            }
        };
    }

    @Nonnull
    private List<String> parseEntryPoints(@Nonnull ApplicationArguments args) {
        var entryOptions = args.getOptionValues("entry");
        if (entryOptions == null || entryOptions.isEmpty()) {
            return List.of();
        }

        List<String> entryPoints = new ArrayList<>();
        for (String entryOption : entryOptions) {
            // Split by comma to support multiple entry points in one option
            String[] entries = entryOption.split(",");
            for (String entry : entries) {
                String trimmed = entry.trim();
                if (!trimmed.isEmpty()) {
                    entryPoints.add(trimmed);
                }
            }
        }

        return entryPoints;
    }
}

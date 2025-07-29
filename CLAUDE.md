# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Java call graph analysis CLI application built with Spring Boot 3.5.4 and SootUp 2.0.0. The project provides sophisticated static analysis capabilities to extract and visualize call graphs from Java applications.

## Features

- **Call Graph Analysis**: Multiple algorithms (CHA, RTA) for static call graph construction
- **Interface Call Resolution**: Automatic detection and expansion of interface implementations for Spring DI pattern analysis
- **Package Filtering**: Focus analysis on specific packages while excluding JDK classes
- **Custom Entry Points**: Support for specifying custom methods as analysis starting points
- **Flexible Input**: Supports JAR files, class files, and directories
- **Multiple Output Formats**: TXT, CSV, and DOT formats for different use cases
- **Spring Boot CLI**: Professional command-line interface with proper exit code handling

## Architecture

**Framework**: Spring Boot 3.5.4 CLI application
- Entry point: `cherry.callgraph.Main` class with Spring Boot context management
- CLI pattern: Uses `ApplicationRunner` and `ExitCodeGenerator` for proper CLI behavior
- Package structure: `cherry.callgraph` as base package with modular sub-packages

**Interface-Based Design**: Clean separation of concerns
- `cherry.callgraph.analyzer`: Core analysis interfaces and data transfer objects
- `cherry.callgraph.sootup`: SootUp-specific implementation
- Dependency injection: Spring Boot manages interface-to-implementation binding

**Analysis Engine**: SootUp 2.0.0 integration
- Static analysis: Multiple algorithms (CHA, RTA) for call graph construction
- Interface resolution: Automatic interface implementation detection for Spring DI patterns
- Scope management: Proper SootUp view configuration with input locations
- Entry point handling: Automatic main method detection and custom entry point support

## Build System

**Gradle 8.14.3** with Gradle Wrapper
- Plugin: `java-library` (not application plugin)
- Spring Boot BOM for dependency management
- Java 21 toolchain with UTF-8 encoding
- Deprecation warnings enabled for code quality

## Source Structure

```
src/main/java/cherry/callgraph/
├── Main.java                    # Spring Boot CLI entry point
├── CallGraphRunner.java         # CLI argument processing and analysis orchestration
├── OutputFormatter.java        # Multi-format output generation
├── analyzer/                    # Analysis interface and data objects
│   ├── CallGraphAnalyzer.java   # Core analysis interface
│   ├── AnalysisResult.java      # Analysis result data
│   ├── ClassInfo.java           # Class information record
│   ├── MethodInfo.java          # Method information record
│   └── CallEdgeInfo.java        # Call edge information record
└── sootup/                      # SootUp-specific implementation
    └── SootUpAnalyzer.java      # SootUp integration and call graph analysis

src/main/resources/
├── application.properties       # Spring Boot configuration and logging
```

## Development Commands

- **Build**: `./gradlew build`
- **Run CLI**: `./gradlew bootRun --args="[options] <files...>"`
- **Create executable JAR**: `./gradlew bootJar`
- **Test**: `./gradlew test`
- **Clean**: `./gradlew clean`

## CLI Usage

```bash
# Basic analysis
./gradlew bootRun --args="application.jar"

# Verbose output
./gradlew bootRun --args="--verbose application.jar"

# Package filtering
./gradlew bootRun --args="--package=cherry.testtool application.jar"

# Class exclusion by FQCN prefix
./gradlew bootRun --args="--exclude=com.example.test application.jar"

# Multiple exclusions
./gradlew bootRun --args="--exclude=com.example.test,org.junit application.jar"

# RTA algorithm for better interface resolution
./gradlew bootRun --args="--algorithm=rta --package=cherry.testtool application.jar"

# Custom entry point analysis
./gradlew bootRun --args="--entry=InvokerController.invoke application.jar"
```

## CLI Options

- `--algorithm=<algo>`: Algorithm: cha, rta (default: cha) - RTA recommended for interface calls
- `--entry=<method>`: Entry point method (default: main methods) - supports ClassName.methodName format
- `--package=<package>`: Filter by package name - recommended for focused analysis
- `--exclude=<class>`: Exclude classes by FQCN prefix - supports class and package exclusion
- `--exclude-jdk`: Exclude JDK classes from analysis (default: false)
- `--quiet`: Suppress standard output
- `--verbose`: Show detailed information
- `--output=<file>`: Output file for call graph (default: stdout)
- `--format=<format>`: Output format: txt, csv, dot (default: txt)
- `--help`: Show help message (PENDING)

## Key Dependencies

- `spring-boot-starter`: Core Spring Boot functionality
- `org.soot-oss:sootup.core`: SootUp core analysis engine
- `org.soot-oss:sootup.java.core`: SootUp Java-specific classes
- `org.soot-oss:sootup.java.bytecode.frontend`: SootUp bytecode frontend
- `org.soot-oss:sootup.callgraph`: SootUp call graph algorithms
- `jakarta.annotation:jakarta.annotation-api`: Annotation support
- Spring Boot BOM manages all version compatibility

## SootUp Integration Details

**Analysis View Configuration**:
- JavaView: Central view for SootUp analysis with input locations
- Input locations: `JavaClassPathAnalysisInputLocation` for JAR/class files
- System exclusions: Standard JDK packages filtered out via `isJdkClass()` method

**Call Graph Construction**:
- Algorithms: CHA (`ClassHierarchyAnalysisAlgorithm`), RTA (`RapidTypeAnalysisAlgorithm`)
- Interface resolution: SootUp automatically handles interface calls (no manual expansion needed)
- Entry points: Automatic main method detection and custom method specification
- Call graph API: `callGraph.callsFrom()` with `getSourceMethodSignature()` and `getTargetMethodSignature()`
- Duplicate removal: `LinkedHashSet` maintains insertion order while removing duplicate call edges

**Supported Input Types**:
- `.jar` files: Added via `JavaClassPathAnalysisInputLocation`
- `.class` files: Added via `JavaClassPathAnalysisInputLocation`
- Directories: Recursively scanned for class files
- `.war` files: PENDING - planned support

## Output Formats

**TXT Format**: Human-readable text output with call edges and class listings (IMPLEMENTED)
**CSV Format**: Structured data with headers for spreadsheet analysis (IMPLEMENTED)
**DOT Format**: Graphviz-compatible format for visual call graph generation (IMPLEMENTED)

Usage examples:
```bash
# CSV format for data analysis
./gradlew bootRun --args="--format=csv --output=callgraph.csv application.jar"

# DOT format for visualization with Graphviz
./gradlew bootRun --args="--format=dot --output=callgraph.dot application.jar"
dot -Tpng callgraph.dot -o callgraph.png
```

## Logging Configuration

- Console output: Message-only format (`%msg%n`)
- Log levels: WARN for most components, INFO for CallGraphRunner and SootUpAnalyzer
- SootUp warnings: Suppressed with `logging.level.sootup=ERROR`
- Package-specific: `cherry.callgraph.sootup.SootUpAnalyzer=INFO` for implementation logging
- Spring Boot banner: Disabled for clean CLI output

## Code Quality Standards

- Java 21 language features (records, switch expressions, var)
- Modern SootUp APIs (2.0.0 compatible methods)
- Stream API and functional programming style preferred over imperative loops
- Proper resource management with try-with-resources
- Comprehensive error handling and logging
- Import statements preferred over FQCN usage
- Unused method parameters should be removed
- Optional.ifPresent() preferred over if-present pattern

## Testing

Run analysis on the built application itself for testing:
```bash
./gradlew build
./gradlew bootRun --args="--verbose build/libs/java-call-graph-plain.jar"
```

Expected output: Call graph showing Main.main -> Main.doMain relationship along with detected classes and methods.

## Filtering and Exclusion

The application provides flexible filtering options for focused analysis:

- **Package Inclusion**: `--package=<package>` filters classes by package prefix for focused analysis
- **Class Exclusion**: `--exclude=<class>` excludes classes by FQCN prefix (supports both specific classes and package prefixes)
- **JDK Exclusion**: `--exclude-jdk` removes standard JDK classes from analysis
- **Filter Precedence**: Exclusion filters are checked first, then inclusion filters are applied
- **Stream API Processing**: All filtering uses functional programming style with Stream API

Example usage:
```bash
# Focus on specific package, exclude test classes
./gradlew bootRun --args="--package=cherry.testtool --exclude=cherry.testtool.test application.jar"

# Exclude multiple packages/classes
./gradlew bootRun --args="--exclude=com.example.test,org.junit,cherry.testtool.Mock application.jar"
```

## Current Implementation Status

**COMPLETED Features:**
- Core call graph analysis with multiple algorithms (CHA, RTA)
- SootUp 2.0.0 integration with automatic interface call resolution
- Interface-based architecture with clean separation of analysis interface and SootUp implementation
- Package filtering and FQCN-based class exclusion (`--exclude=<class>`)
- Custom entry point specification
- Multiple output formats (TXT, CSV, DOT)
- Output file option (--output=<file>)
- Duplicate call edge removal with insertion order preservation
- Comprehensive logging and error handling with SootUp warning suppression
- Stream API implementation for functional programming style
- Proper SootUp Call API usage (`getSourceMethodSignature()`, `getTargetMethodSignature()`)
- CallEdgeInfo properties aligned with SootUp naming (source/target instead of caller/target)
- Modular package structure: `analyzer` for interfaces/DTOs, `sootup` for implementation

**PENDING Features (Low Priority):**
- WAR file support
- Comprehensive help system (--help)
- Entry point prioritization in call edge ordering
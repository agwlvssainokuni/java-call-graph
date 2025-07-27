# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Java call graph analysis CLI application built with Spring Boot 3.5.4 and WALA (Watson Libraries for Analysis) 1.6.10. The project provides sophisticated static analysis capabilities to extract and visualize call graphs from Java applications.

## Features

- **Call Graph Analysis**: Uses WALA's Class Hierarchy Analysis (CHA) for static call graph construction
- **Multiple Output Formats**: Support for TXT, CSV, and DOT (Graphviz) output formats
- **Entry Point Detection**: Automatic detection of main methods as analysis entry points
- **Flexible Input**: Supports JAR files, WAR files, class files, and directories
- **Spring Boot CLI**: Professional command-line interface with proper exit code handling

## Architecture

**Framework**: Spring Boot 3.5.4 CLI application
- Entry point: `cherry.callgraph.Main` class with Spring Boot context management
- CLI pattern: Uses `ApplicationRunner` and `ExitCodeGenerator` for proper CLI behavior
- Package structure: `cherry.callgraph` as base package

**Analysis Engine**: WALA 1.6.10 integration
- Static analysis: Class Hierarchy Analysis (CHA) for call graph construction
- Scope management: Proper WALA scope configuration with exclusions
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
├── WalaAnalyzer.java           # WALA integration and call graph analysis
└── OutputFormatter.java        # Multi-format output generation

src/main/resources/
├── application.properties       # Spring Boot configuration
├── scope.txt                   # WALA analysis scope configuration
└── Java60RegressionExclusions.txt  # WALA system library exclusions
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

# CSV format output
./gradlew bootRun --args="--format=csv --output=callgraph.csv application.jar"

# DOT format for visualization
./gradlew bootRun --args="--format=dot --output=callgraph.dot application.jar"
```

## CLI Options

- `--output=<file>`: Output file for call graph (default: stdout)
- `--format=<format>`: Output format: txt, csv, dot (default: txt)
- `--algorithm=<algo>`: Algorithm: cha, rta, 0cfa (default: cha) 
- `--entry=<method>`: Entry point method (default: main methods)
- `--package=<package>`: Filter by package name
- `--exclude-jdk`: Exclude JDK classes from analysis
- `--quiet`: Suppress standard output
- `--verbose`: Show detailed information
- `--help`: Show help message

## Key Dependencies

- `spring-boot-starter`: Core Spring Boot functionality
- `com.ibm.wala:com.ibm.wala.core`: WALA core analysis engine
- `com.ibm.wala:com.ibm.wala.util`: WALA utility classes
- `com.ibm.wala:com.ibm.wala.shrike`: WALA bytecode analysis
- `jakarta.annotation:jakarta.annotation-api`: Annotation support
- Spring Boot BOM manages all version compatibility

## WALA Integration Details

**Analysis Scope Configuration**:
- Primordial loader: Java standard library
- Application loader: Target application classes
- System exclusions: Standard JDK packages filtered out

**Call Graph Construction**:
- Algorithm: Class Hierarchy Analysis (CHA) via `Util.makeZeroCFABuilder`
- Entry points: Automatic main method detection
- Cache: `AnalysisCacheImpl` for performance optimization

**Supported Input Types**:
- `.jar` files: Added via `scope.addToScope()`
- `.war` files: Treated as JAR files
- `.class` files: Added via `scope.addClassFileToScope()`
- Directories: Recursively scanned for class files

## Output Formats

**TXT Format**: Human-readable text output with call edges and class listings
**CSV Format**: Structured data with headers for spreadsheet analysis
**DOT Format**: Graphviz-compatible format for visual call graph generation

## Logging Configuration

- Console output: Message-only format (`%msg%n`)
- Log levels: WARN for most components, INFO for CallGraphRunner
- Spring Boot banner: Disabled for clean CLI output

## Code Quality Standards

- Java 21 language features (records, switch expressions, var)
- Modern WALA APIs (non-deprecated methods)
- Enhanced for-loops over iterators
- Proper resource management with try-with-resources
- Comprehensive error handling and logging

## Testing

Run analysis on the built application itself for testing:
```bash
./gradlew build
./gradlew bootRun --args="--verbose build/libs/java-call-graph-plain.jar"
```

Expected output: Call graph showing Main.main -> Main.doMain relationship along with detected classes and methods.
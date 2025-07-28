# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Java call graph analysis CLI application built with Spring Boot 3.5.4 and WALA (Watson Libraries for Analysis) 1.6.10. The project provides sophisticated static analysis capabilities to extract and visualize call graphs from Java applications.

## Features

- **Call Graph Analysis**: Multiple algorithms (CHA, RTA, 0-CFA) for static call graph construction
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
- Package structure: `cherry.callgraph` as base package

**Analysis Engine**: WALA 1.6.10 integration
- Static analysis: Multiple algorithms (CHA, RTA, 0-CFA) for call graph construction
- Interface resolution: Automatic interface implementation detection for Spring DI patterns
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

# Package filtering
./gradlew bootRun --args="--package=cherry.testtool application.jar"

# RTA algorithm for better interface resolution
./gradlew bootRun --args="--algorithm=rta --package=cherry.testtool application.jar"

# Custom entry point analysis
./gradlew bootRun --args="--entry=InvokerController.invoke application.jar"
```

## CLI Options

- `--algorithm=<algo>`: Algorithm: cha, rta, 0cfa (default: cha) - RTA recommended for interface calls
- `--entry=<method>`: Entry point method (default: main methods) - supports ClassName.methodName format
- `--package=<package>`: Filter by package name - recommended for focused analysis
- `--exclude-jdk`: Exclude JDK classes from analysis (default: false)
- `--quiet`: Suppress standard output
- `--verbose`: Show detailed information
- `--output=<file>`: Output file for call graph (default: stdout)
- `--format=<format>`: Output format: txt, csv, dot (default: txt)
- `--help`: Show help message (PENDING)

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
- Algorithms: CHA (`Util.makeZeroCFABuilder`), RTA (`Util.makeRTABuilder`), 0-CFA (`Util.makeZeroOneCFABuilder`)
- Interface resolution: `expandEntryPointsWithImplementations()` adds concrete implementations as entry points
- Entry points: Automatic main method detection and custom method specification
- Cache: `AnalysisCacheImpl` for performance optimization

**Supported Input Types**:
- `.jar` files: Added via `scope.addToScope()`
- `.class` files: Added via `scope.addClassFileToScope()`
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
- Log levels: WARN for most components, INFO for CallGraphRunner
- Spring Boot banner: Disabled for clean CLI output

## Code Quality Standards

- Java 21 language features (records, switch expressions, var)
- Modern WALA APIs (non-deprecated methods)
- Enhanced for-loops over iterators
- Proper resource management with try-with-resources
- Comprehensive error handling and logging
- Import statements preferred over FQCN usage
- Unused method parameters should be removed

## Testing

Run analysis on the built application itself for testing:
```bash
./gradlew build
./gradlew bootRun --args="--verbose build/libs/java-call-graph-plain.jar"
```

Expected output: Call graph showing Main.main -> Main.doMain relationship along with detected classes and methods.

## Interface Call Resolution

The application includes sophisticated interface call resolution for Spring Dependency Injection patterns:

- **Precise Interface Analysis**: `addImplementationsForInterfaces()` method identifies interfaces used by entry point classes and adds their concrete implementations as additional entry points
- **Implementation Detection**: Uses WALA's `getImplementors()` to find all concrete implementations of interfaces
- **Entry Point Expansion**: `expandEntryPointsWithImplementations()` method in `WalaAnalyzer.java:441-462`
- **Algorithm Recommendation**: Use RTA algorithm (`--algorithm=rta`) for better interface call resolution
- **Package Filtering Integration**: Interface resolution respects package filters for focused analysis

Example usage for Spring applications:
```bash
# Analyze Spring application with interface resolution
./gradlew bootRun --args="--algorithm=rta --package=cherry.testtool ../cherry-testtool/lib/build/libs/lib-plain.jar"

# Expected: Controller → Service interface calls properly resolved (e.g., 8 call edges found)
```

## Current Implementation Status

**COMPLETED Features:**
- Core call graph analysis with multiple algorithms (CHA, RTA, 0-CFA)
- Precise interface call resolution for Spring DI patterns
- Package filtering and JDK exclusion
- Custom entry point specification
- Multiple output formats (TXT, CSV, DOT)
- Output file option (--output=<file>)
- Comprehensive logging and error handling

**PENDING Features (Low Priority):**
- WAR file support
- Comprehensive help system (--help)
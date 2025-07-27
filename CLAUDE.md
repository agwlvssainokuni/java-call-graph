# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Java call graph analysis CLI application built with Spring Boot 3.5.4. The project uses a Spring Boot CLI pattern with proper exit code handling for command-line execution.

## Architecture

**Framework**: Spring Boot 3.5.4 CLI application
- Entry point: `cherry.callgraph.Main` class with Spring Boot context management
- CLI pattern: Uses `ApplicationRunner` and `ExitCodeGenerator` for proper CLI behavior
- Package structure: `cherry.callgraph` as base package

## Build System

**Gradle 8.14.3** with Gradle Wrapper
- Plugin: `java-library` (not application plugin)
- Spring Boot BOM for dependency management
- Java 21 toolchain with UTF-8 encoding

## Source Structure

```
src/main/java/cherry/callgraph/
├── Main.java                    # Spring Boot CLI entry point
└── [future CLI components]
```

## Development Commands

- **Build**: `./gradlew build`
- **Run CLI**: `./gradlew bootRun`
- **Create executable JAR**: `./gradlew bootJar`
- **Test**: `./gradlew test`
- **Clean**: `./gradlew clean`

## CLI Execution

The application follows Spring Boot CLI patterns:
- Main class manages Spring context lifecycle
- Proper exit code handling for CLI tools
- Can be run via `./gradlew bootRun` or as standalone JAR

## Key Dependencies

- `spring-boot-starter`: Core Spring Boot functionality
- Spring Boot BOM manages all version compatibility
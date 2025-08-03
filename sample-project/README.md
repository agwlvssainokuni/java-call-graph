# Sample Projects for Call Graph Analysis

This directory contains sample Java applications designed to demonstrate the call graph analysis tool's capabilities.

## Projects

### 1. cli-app - Spring Boot CLI Application
A command-line file processing utility that demonstrates layered architecture with at least 4 levels of method calls:
- Main → CLI Handler → Service Layer → Repository Layer → File System Operations

### 2. web-app - Spring Boot Web Application  
A REST API application packaged as an executable JAR that demonstrates web application call patterns:
- Controller → Service → Repository → Database/External Service calls

### 3. war-app - Traditional WAR Application
A servlet-based web application deployable to Tomcat that demonstrates traditional Java web patterns:
- Servlet → Service → DAO → Database Operations

## Usage

Each project can be built and analyzed independently:

```bash
# Build a sample project
cd sample-project/cli-app
./gradlew build

# Analyze with the call graph tool
cd ../../
./gradlew bootRun --args="--verbose sample-project/cli-app/build/libs/cli-app.jar"
```

## Call Graph Analysis Features Demonstrated

- **Multiple call levels**: Each project has at least 4 levels of method call hierarchy
- **Interface resolution**: Spring dependency injection and interface implementations
- **Package filtering**: Projects use distinct package names for filtering demonstrations
- **Different entry points**: Various main methods and web endpoints as analysis starting points
# Java Call Graph Analysis Tool

[![Java Version](https://img.shields.io/badge/Java-21-orange)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen)](https://spring.io/projects/spring-boot)
[![WALA](https://img.shields.io/badge/WALA-1.6.10-blue)](https://wala.sourceforge.io/)
[![Gradle](https://img.shields.io/badge/Gradle-8.14.3-brightgreen)](https://gradle.org/)

A sophisticated command-line tool for static call graph analysis of Java applications using IBM's WALA (Watson Libraries for Analysis) framework and Spring Boot.

## Features

- **Multiple Analysis Algorithms**: CHA (Class Hierarchy Analysis), RTA (Rapid Type Analysis), and 0-CFA
- **Interface Call Resolution**: Automatic detection and expansion of interface implementations for Spring DI pattern analysis
- **Package Filtering**: Focus analysis on specific packages while excluding JDK classes
- **Custom Entry Points**: Support for specifying custom methods as analysis starting points
- **Flexible Input Support**: JAR files, class files, and directories
- **Multiple Output Formats**: TXT (human-readable), CSV (data analysis), and DOT (visualization)
- **Professional CLI**: Built with Spring Boot for robust command-line interface with proper exit codes

## Quick Start

### Prerequisites

- Java 21 or higher
- Gradle 8.14.3 or higher (included via Gradle Wrapper)

### Build and Run

```bash
# Clone the repository
git clone https://github.com/agwlvssainokuni/java-call-graph.git
cd java-call-graph

# Build the project
./gradlew build

# Run basic analysis
./gradlew bootRun --args="your-application.jar"

# Create executable JAR
./gradlew bootJar
java -jar build/libs/java-call-graph-*.jar your-application.jar
```

## Usage Examples

### Basic Analysis
```bash
# Analyze a JAR file
./gradlew bootRun --args="application.jar"

# Verbose output with detailed information
./gradlew bootRun --args="--verbose application.jar"
```

### Package Filtering
```bash
# Focus analysis on specific package
./gradlew bootRun --args="--package=com.example application.jar"

# Multiple packages
./gradlew bootRun --args="--package=com.example,org.mycompany application.jar"
```

### Algorithm Selection
```bash
# Use RTA algorithm (recommended for interface resolution)
./gradlew bootRun --args="--algorithm=rta --package=com.example application.jar"

# Use 0-CFA for more precise analysis
./gradlew bootRun --args="--algorithm=0cfa application.jar"
```

### Custom Entry Points
```bash
# Specify custom entry point method
./gradlew bootRun --args="--entry=Controller.handleRequest application.jar"

# Multiple entry points
./gradlew bootRun --args="--entry=Controller.handleRequest,Service.processData application.jar"
```

### Output Formats
```bash
# CSV format for spreadsheet analysis
./gradlew bootRun --args="--format=csv --output=callgraph.csv application.jar"

# DOT format for Graphviz visualization
./gradlew bootRun --args="--format=dot --output=callgraph.dot application.jar"
dot -Tpng callgraph.dot -o callgraph.png
```

## Command Line Options

| Option | Description | Default |
|--------|-------------|---------|
| `--algorithm=<algo>` | Analysis algorithm: `cha`, `rta`, `0cfa` | `cha` |
| `--entry=<method>` | Entry point method (ClassName.methodName format) | main methods |
| `--package=<package>` | Filter by package name (comma-separated) | all packages |
| `--exclude-jdk` | Exclude JDK classes from analysis | `false` |
| `--output=<file>` | Output file for call graph | stdout |
| `--format=<format>` | Output format: `txt`, `csv`, `dot` | `txt` |
| `--quiet` | Suppress standard output | `false` |
| `--verbose` | Show detailed information | `false` |
| `--help` | Show help message | - |

## Architecture

### Core Components

- **Main.java**: Spring Boot CLI entry point with proper context management
- **CallGraphRunner.java**: CLI argument processing and analysis orchestration  
- **WalaAnalyzer.java**: WALA integration and call graph analysis engine
- **OutputFormatter.java**: Multi-format output generation (TXT, CSV, DOT)

### Analysis Engine

Built on IBM WALA 1.6.10 with sophisticated features:

- **Static Analysis**: Multiple algorithms for different precision/performance trade-offs
- **Interface Resolution**: Automatic detection of interface implementations for Spring DI patterns
- **Scope Management**: Proper WALA scope configuration with system library exclusions
- **Entry Point Handling**: Automatic main method detection and custom entry point support

## Supported Input Types

- **JAR files** (`.jar`): Standard Java archive files
- **Class files** (`.class`): Individual compiled Java classes
- **Directories**: Recursively scanned for class files
- **WAR files** (`.war`): Web application archives *(planned)*

## Output Formats

### TXT Format
Human-readable text output showing call edges and class information:
```
=== Call Graph Analysis Results ===

Call Graph (5 edges):
  Lcom/example/Main.main -> Lcom/example/Service.process
  Lcom/example/Service.process -> Lcom/example/Repository.save
  ...

Classes (3):
  Lcom/example/Main
  Lcom/example/Service
  Lcom/example/Repository
```

### CSV Format
Structured data suitable for spreadsheet analysis:
```csv
caller_class,caller_method,target_class,target_method
"Lcom/example/Main","main","Lcom/example/Service","process"
"Lcom/example/Service","process","Lcom/example/Repository","save"
```

### DOT Format
Graphviz-compatible format for visual call graph generation:
```dot
digraph CallGraph {
  rankdir=LR;
  node [shape=box, style=rounded];
  
  "com.example.Main.main" -> "com.example.Service.process";
  "com.example.Service.process" -> "com.example.Repository.save";
}
```

## Interface Call Resolution

The tool includes sophisticated interface call resolution for Spring Dependency Injection patterns:

- **Precise Interface Analysis**: Identifies interfaces used by entry point classes
- **Implementation Detection**: Finds all concrete implementations using WALA's class hierarchy
- **Entry Point Expansion**: Adds interface implementations as additional entry points
- **Algorithm Recommendation**: Use RTA algorithm (`--algorithm=rta`) for better interface resolution

Example for Spring applications:
```bash
./gradlew bootRun --args="--algorithm=rta --package=com.example spring-app.jar"
```

## Build System

- **Gradle 8.14.3** with Gradle Wrapper
- **Java 21** toolchain with UTF-8 encoding
- **Spring Boot BOM** for dependency management
- **Code Quality**: Deprecation warnings and unchecked operation warnings enabled

## Dependencies

- **Spring Boot 3.5.4**: Core framework and CLI infrastructure
- **IBM WALA 1.6.10**: Static analysis engine
  - `com.ibm.wala.core`: Core analysis functionality
  - `com.ibm.wala.util`: Utility classes
  - `com.ibm.wala.shrike`: Bytecode analysis
- **Jakarta Annotations**: Standard annotations support

## Development

### Building
```bash
./gradlew build
```

### Testing
```bash
./gradlew test
```

### Creating Distribution
```bash
./gradlew bootJar
```

### Self-Analysis Testing
Test the tool on itself:
```bash
./gradlew build
./gradlew bootRun --args="--verbose build/libs/java-call-graph-plain.jar"
```

## Code Quality Standards

- Java 21 language features (records, switch expressions, var)
- Modern WALA APIs (non-deprecated methods)
- Enhanced for-loops over iterators
- Proper resource management with try-with-resources
- Comprehensive error handling and logging
- Import statements preferred over FQCN usage
- Unused method parameters removed

## License

Licensed under the Apache License, Version 2.0. See the LICENSE file for details.

## Contributing

Contributions are welcome! Please read the contributing guidelines and submit pull requests for any improvements.

## Support

For issues and questions, please use the GitHub issue tracker.
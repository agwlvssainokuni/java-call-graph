# Java Call Graph Analysis Tool

[![Java Version](https://img.shields.io/badge/Java-21-orange)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen)](https://spring.io/projects/spring-boot)
[![SootUp](https://img.shields.io/badge/SootUp-2.0.0-blue)](https://github.com/soot-oss/SootUp)
[![Gradle](https://img.shields.io/badge/Gradle-8.14.3-brightgreen)](https://gradle.org/)

A sophisticated command-line tool for static call graph analysis of Java applications using SootUp 2.0.0 framework and Spring Boot.

## Features

- **Multiple Analysis Algorithms**: CHA (Class Hierarchy Analysis) and RTA (Rapid Type Analysis)
- **Automatic Interface Resolution**: SootUp 2.0.0 handles interface calls automatically
- **Flexible Filtering**: Package-based inclusion and FQCN-based class exclusion
- **Custom Entry Points**: Support for specifying custom methods as analysis starting points
- **Flexible Input Support**: JAR files, class files, and directories
- **Multiple Output Formats**: TXT (human-readable), CSV (data analysis), JSON (programmatic processing), and DOT (visualization)
- **Duplicate Removal**: Call edges are deduplicated while maintaining insertion order
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

### Package Filtering and Class Exclusion
```bash
# Focus analysis on specific package
./gradlew bootRun --args="--package=com.example application.jar"

# Multiple packages
./gradlew bootRun --args="--package=com.example,org.mycompany application.jar"

# Exclude specific classes by FQCN prefix
./gradlew bootRun --args="--exclude=com.example.test application.jar"

# Exclude multiple classes/packages
./gradlew bootRun --args="--exclude=com.example.test,org.junit application.jar"

# Combine filtering and exclusion
./gradlew bootRun --args="--package=com.example --exclude=com.example.test application.jar"
```

### Algorithm Selection
```bash
# Use RTA algorithm (recommended for interface resolution)
./gradlew bootRun --args="--algorithm=rta --package=com.example application.jar"

# Use CHA algorithm (faster but less precise)
./gradlew bootRun --args="--algorithm=cha application.jar"
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

# JSON format for programmatic processing
./gradlew bootRun --args="--format=json --output=callgraph.json application.jar"

# DOT format for Graphviz visualization
./gradlew bootRun --args="--format=dot --output=callgraph.dot application.jar"
dot -Tpng callgraph.dot -o callgraph.png
```

## Command Line Options

| Option | Description | Default |
|--------|-------------|---------|
| `--algorithm=<algo>` | Analysis algorithm: `cha`, `rta` | `cha` |
| `--entry=<method>` | Entry point method (ClassName.methodName format) | main methods |
| `--package=<package>` | Filter by package name (comma-separated) | all packages |
| `--exclude=<class>` | Exclude classes by FQCN prefix (comma-separated) | none |
| `--exclude-jdk` | Exclude JDK classes from analysis | `false` |
| `--output=<file>` | Output file for call graph | stdout |
| `--format=<format>` | Output format: `txt`, `csv`, `json`, `dot` | `txt` |
| `--quiet` | Suppress standard output | `false` |
| `--verbose` | Show detailed information | `false` |
| `--help` | Show help message | - |

## Architecture

### Core Components

- **Main.java**: Spring Boot CLI entry point with proper context management
- **CallGraphRunner.java**: CLI argument processing and analysis orchestration  
- **output/OutputFormatter.java**: Multi-format output generation (TXT, CSV, JSON, DOT)
- **output/Format.java**: Output format enum

### Interface-Based Architecture

- **analyze/** package: Core analysis interfaces and data transfer objects
  - `CallGraphAnalyzer.java`: Analysis interface defining the contract
  - `Algorithm.java`: Analysis algorithm enum
  - `AnalysisResult.java`, `ClassInfo.java`, `MethodInfo.java`, `CallEdgeInfo.java`: Data records
- **output/** package: Output formatting and format definitions
  - `OutputFormatter.java`: Multi-format output generation
  - `Format.java`: Output format enum
- **sootup/** package: SootUp-specific implementation
  - `SootUpAnalyzer.java`: SootUp integration and call graph analysis engine
- **Dependency Injection**: Spring Boot manages interface-to-implementation binding

### Analysis Engine

Built on SootUp 2.0.0 with sophisticated features:

- **Static Analysis**: Multiple algorithms for different precision/performance trade-offs
- **Interface Resolution**: SootUp automatically handles interface calls (no manual expansion needed)
- **Call Graph API**: Uses `getSourceMethodSignature()` and `getTargetMethodSignature()` for proper call edge extraction
- **Duplicate Removal**: `LinkedHashSet` preserves insertion order while removing duplicate call edges
- **View Management**: Proper SootUp JavaView configuration with input locations
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
  com.example.Main.main -> com.example.Service.process
  com.example.Service.process -> com.example.Repository.save
  ...

Classes (3):
  com.example.Main
  com.example.Service
  com.example.Repository
```

### CSV Format
Structured call edge data suitable for spreadsheet analysis (only call edges, no verbose mode):
```csv
source_class,source_method,target_class,target_method
"com.example.Main","main","com.example.Service","process"
"com.example.Service","process","com.example.Repository","save"
```

### JSON Format
Structured JSON output for programmatic processing and API integration:
```json
{
  "callEdges": [
    {
      "sourceClass": "com.example.Main",
      "sourceMethod": "main",
      "targetClass": "com.example.Service",
      "targetMethod": "process"
    },
    {
      "sourceClass": "com.example.Service",  
      "sourceMethod": "process",
      "targetClass": "com.example.Repository",
      "targetMethod": "save"
    }
  ]
}
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

## Filtering and Exclusion

The tool provides flexible filtering options for focused analysis:

- **Package Inclusion**: Use `--package=<package>` to focus on specific packages
- **Class Exclusion**: Use `--exclude=<class>` for FQCN-based exclusion (supports both specific classes and package prefixes)
- **Filter Precedence**: Exclusion filters are checked first, then inclusion filters are applied
- **JDK Exclusion**: Use `--exclude-jdk` to remove standard library classes
- **Algorithm Recommendation**: Use RTA algorithm (`--algorithm=rta`) for better interface call resolution

Example combinations:
```bash
# Focus on business logic, exclude tests
./gradlew bootRun --args="--package=com.example --exclude=com.example.test spring-app.jar"

# Exclude multiple test frameworks
./gradlew bootRun --args="--exclude=org.junit,org.mockito,com.example.Mock spring-app.jar"
```

## Build System

- **Gradle 8.14.3** with Gradle Wrapper
- **Java 21** toolchain with UTF-8 encoding
- **Spring Boot BOM** for dependency management
- **Code Quality**: Deprecation warnings and unchecked operation warnings enabled

## Dependencies

- **Spring Boot 3.5.4**: Core framework and CLI infrastructure
- **SootUp 2.0.0**: Static analysis engine
  - `sootup.core`: Core analysis functionality
  - `sootup.java.core`: Java-specific classes
  - `sootup.java.bytecode.frontend`: Bytecode frontend
  - `sootup.callgraph`: Call graph algorithms
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
- Modern SootUp APIs (2.0.0 compatible methods)
- Stream API and functional programming style preferred over imperative loops
- Proper resource management with try-with-resources
- Comprehensive error handling and logging
- Import statements preferred over FQCN usage
- Unused method parameters removed
- Optional.ifPresent() preferred over if-present pattern

## License

Licensed under the Apache License, Version 2.0. See the LICENSE file for details.

## Contributing

Contributions are welcome! Please read the contributing guidelines and submit pull requests for any improvements.

## Support

For issues and questions, please use the GitHub issue tracker.
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

import cherry.callgraph.analyze.AnalysisResult;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class OutputFormatter {

    public enum Format {
        TXT, CSV, DOT
    }

    public void writeOutput(
            @Nonnull AnalysisResult result,
            @Nonnull Format format,
            String outputFile,
            boolean verbose
    ) throws IOException {
        if (outputFile != null) {
            Path outputPath = Paths.get(outputFile);
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputPath))) {
                writeFormatted(writer, result, format, verbose);
            }
        } else {
            writeFormatted(new PrintWriter(System.out, true), result, format, verbose);
        }
    }

    private void writeFormatted(
            @Nonnull PrintWriter writer,
            @Nonnull AnalysisResult result,
            @Nonnull Format format,
            boolean verbose
    ) {
        switch (format) {
            case TXT -> writeTextFormat(writer, result, verbose);
            case CSV -> writeCsvFormat(writer, result, verbose);
            case DOT -> writeDotFormat(writer, result);
        }
    }

    private void writeTextFormat(
            @Nonnull PrintWriter writer,
            @Nonnull AnalysisResult result,
            boolean verbose
    ) {
        writer.println("=== Call Graph Analysis Results ===");
        writer.println();

        // Call graph edges
        writer.printf("Call Graph (%d edges):%n", result.callEdges().size());
        for (var callEdge : result.callEdges()) {
            writer.printf("  %s.%s -> %s.%s%n",
                    callEdge.sourceClass(),
                    callEdge.sourceMethod(),
                    callEdge.targetClass(),
                    callEdge.targetMethod()
            );
        }

        if (verbose) {
            writer.println();
            writer.println("Classes found:");
            for (var classInfo : result.classes()) {
                String type = classInfo.isInterface() ? "interface" :
                        classInfo.isAbstract() ? "abstract class" : "class";
                writer.printf("  %s (%s)%n", classInfo.name(), type);
            }

            writer.println();
            writer.println("Methods found:");
            for (var methodInfo : result.methods()) {
                String visibility = methodInfo.isPublic() ? "public" :
                        methodInfo.isPrivate() ? "private" : "package";
                String modifier = methodInfo.isStatic() ? "static" : "instance";
                writer.printf("  %s.%s (%s %s)%n",
                        methodInfo.className(),
                        methodInfo.methodName(),
                        visibility,
                        modifier
                );
            }
        } else {
            writer.println();
            writer.printf("Classes (%d):%n", result.classes().size());
            for (var classInfo : result.classes()) {
                writer.printf("  %s%n", classInfo.name());
            }
        }
    }

    private void writeCsvFormat(
            @Nonnull PrintWriter writer,
            @Nonnull AnalysisResult result,
            boolean verbose
    ) {
        // CSV header for call edges
        writer.println("caller_class,caller_method,target_class,target_method");

        // Call edges data
        for (var callEdge : result.callEdges()) {
            writer.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    escapeCsv(callEdge.sourceClass()),
                    escapeCsv(callEdge.sourceMethod()),
                    escapeCsv(callEdge.targetClass()),
                    escapeCsv(callEdge.targetMethod())
            );
        }

        if (verbose) {
            writer.println();
            writer.println("class_name,type,is_interface,is_abstract");
            for (var classInfo : result.classes()) {
                String type = classInfo.isInterface() ? "interface" :
                        classInfo.isAbstract() ? "abstract class" : "class";
                writer.printf("\"%s\",\"%s\",%b,%b%n",
                        escapeCsv(classInfo.name()),
                        type,
                        classInfo.isInterface(),
                        classInfo.isAbstract()
                );
            }

            writer.println();
            writer.println("class_name,method_name,signature,visibility,modifier");
            for (var methodInfo : result.methods()) {
                String visibility = methodInfo.isPublic() ? "public" :
                        methodInfo.isPrivate() ? "private" : "package";
                String modifier = methodInfo.isStatic() ? "static" : "instance";
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        escapeCsv(methodInfo.className()),
                        escapeCsv(methodInfo.methodName()),
                        escapeCsv(methodInfo.signature()),
                        visibility,
                        modifier
                );
            }
        }
    }

    private void writeDotFormat(
            @Nonnull PrintWriter writer,
            @Nonnull AnalysisResult result
    ) {
        writer.println("digraph CallGraph {");
        writer.println("  rankdir=LR;");
        writer.println("  node [shape=box, style=rounded];");
        writer.println();

        // Collect unique nodes
        var uniqueNodes = new java.util.HashSet<String>();
        for (var callEdge : result.callEdges()) {
            uniqueNodes.add(formatDotNode(callEdge.sourceClass(), callEdge.sourceMethod()));
            uniqueNodes.add(formatDotNode(callEdge.targetClass(), callEdge.targetMethod()));
        }

        // Create nodes for all methods involved in call edges
        for (String nodeId : uniqueNodes) {
            writer.printf("  \"%s\" [label=\"%s\"];%n",
                    nodeId,
                    nodeId
            );
        }

        writer.println();

        // Create edges
        for (var callEdge : result.callEdges()) {
            String sourceNode = formatDotNode(callEdge.sourceClass(), callEdge.sourceMethod());
            String targetNode = formatDotNode(callEdge.targetClass(), callEdge.targetMethod());

            writer.printf("  \"%s\" -> \"%s\";%n", sourceNode, targetNode);
        }

        writer.println("}");
    }

    private String escapeCsv(@Nonnull String value) {
        return value.replace("\"", "\"\"");
    }

    private String formatDotNode(@Nonnull String className, @Nonnull String methodName) {
        return className + "." + methodName;
    }
}

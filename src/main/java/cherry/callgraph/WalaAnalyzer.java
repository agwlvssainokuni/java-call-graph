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

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.core.util.config.AnalysisScopeReader;
import com.ibm.wala.core.util.io.FileProvider;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.shrike.shrikeCT.InvalidClassFileException;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

@Component
public class WalaAnalyzer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public enum Algorithm {
        CHA, RTA, ZERO_CFA
    }

    @Nonnull
    public AnalysisResult analyzeFiles(@Nonnull List<String> filePaths, boolean verbose, @Nonnull List<String> packageFilters, @Nonnull Algorithm algorithm) throws IOException, ClassHierarchyException, CallGraphBuilderCancelException {
        logger.info("Initializing WALA analysis for {} files", filePaths.size());

        // Create analysis scope
        var scope = AnalysisScopeReader.instance.readJavaScope(
                "scope.txt",
                new FileProvider().getFile("Java60RegressionExclusions.txt"),
                WalaAnalyzer.class.getClassLoader()
        );

        // Add input files to scope
        for (String filePath : filePaths) {
            addFileToScope(scope, filePath, verbose);
        }

        if (verbose) {
            logger.info("Analysis scope created with {} loaders", scope.getNumberOfLoaders());
        }

        // Build class hierarchy
        logger.info("Building class hierarchy...");
        IClassHierarchy classHierarchy = ClassHierarchyFactory.make(scope);

        if (verbose) {
            logger.info("Class hierarchy built with {} classes", classHierarchy.getNumberOfClasses());
        }

        // Find entry points (main methods)
        logger.info("Finding entry points...");
        Iterable<Entrypoint> entrypoints = findEntryPoints(classHierarchy, verbose);

        // Build call graph using specified algorithm
        logger.info("Building call graph with {}...", algorithm);
        var options = new AnalysisOptions(scope, entrypoints);
        IAnalysisCacheView cache = new AnalysisCacheImpl();
        var callGraphBuilder = createCallGraphBuilder(algorithm, options, cache, classHierarchy);
        CallGraph callGraph = callGraphBuilder.makeCallGraph(options, null);

        if (verbose) {
            logger.info("Call graph built with {} nodes", callGraph.getNumberOfNodes());
        }

        // Collect classes, methods, and call graph information
        var result = collectAnalysisResults(classHierarchy, callGraph, verbose, packageFilters);

        logger.info("Analysis completed: {} classes, {} methods, {} call edges found",
                result.classes().size(), result.methods().size(), result.callEdges().size());

        return result;
    }

    private void addFileToScope(
            @Nonnull AnalysisScope scope,
            @Nonnull String filePath,
            boolean verbose
    ) throws IOException {
        Path path = Paths.get(filePath);
        File file = path.toFile();

        if (Files.isDirectory(path)) {
            if (verbose) {
                logger.debug("Adding directory to scope: {}", filePath);
            }
            // Find all .class files in directory and subdirectories
            try (var stream = Files.walk(path)) {
                stream.filter(Files::isRegularFile)
                        .filter(p -> p.toString().toLowerCase().endsWith(".class"))
                        .forEach(classPath -> {
                            try {
                                scope.addClassFileToScope(
                                        scope.getApplicationLoader(),
                                        classPath.toFile()
                                );
                                if (verbose) {
                                    logger.debug("  Added class file: {}", classPath);
                                }
                            } catch (InvalidClassFileException e) {
                                logger.warn("Failed to add class file to scope: {} - {}", classPath, e.getMessage());
                            }
                        });
            }
        } else if (Files.isRegularFile(path)) {
            String fileName = path.getFileName().toString().toLowerCase();
            if (fileName.endsWith(".jar") || fileName.endsWith(".war")) {
                if (verbose) {
                    logger.debug("Adding JAR/WAR to scope: {}", filePath);
                }
                JarFile jarFile = new JarFile(file);
                scope.addToScope(
                        scope.getApplicationLoader(),
                        jarFile
                );
            } else if (fileName.endsWith(".class")) {
                if (verbose) {
                    logger.debug("Adding class file to scope: {}", filePath);
                }
                try {
                    scope.addClassFileToScope(
                            scope.getApplicationLoader(),
                            file
                    );
                } catch (InvalidClassFileException e) {
                    logger.warn("Failed to add class file to scope: {} - {}", filePath, e.getMessage());
                }
            }
        }
    }

    @Nonnull
    private Iterable<Entrypoint> findEntryPoints(@Nonnull IClassHierarchy classHierarchy, boolean verbose) {
        List<Entrypoint> entrypoints = new ArrayList<>();

        for (IClass clazz : classHierarchy) {
            // Skip system classes
            if (clazz.getName().toString().startsWith("Ljava/") ||
                    clazz.getName().toString().startsWith("Lsun/") ||
                    clazz.getName().toString().startsWith("Lcom/sun/") ||
                    clazz.getName().toString().startsWith("Ljavax/")) {
                continue;
            }

            // Look for main methods
            for (IMethod method : clazz.getDeclaredMethods()) {
                if (method.getName().toString().equals("main") &&
                        method.isStatic() &&
                        method.isPublic()) {

                    entrypoints.add(new DefaultEntrypoint(method, classHierarchy));
                    if (verbose) {
                        logger.info("Found entry point: {}.main", clazz.getName());
                    }
                }
            }
        }

        if (entrypoints.isEmpty()) {
            logger.warn("No main methods found as entry points");
        } else {
            logger.info("Found {} entry point(s)", entrypoints.size());
        }

        return entrypoints;
    }

    @Nonnull
    private AnalysisResult collectAnalysisResults(
            @Nonnull IClassHierarchy classHierarchy,
            @Nonnull CallGraph callGraph,
            boolean verbose,
            @Nonnull List<String> packageFilters
    ) {
        List<ClassInfo> classes = new ArrayList<>();
        List<MethodInfo> methods = new ArrayList<>();
        List<CallEdgeInfo> callEdges = new ArrayList<>();

        // Collect classes and methods
        for (IClass clazz : classHierarchy) {
            // Skip system classes
            if (clazz.getName().toString().startsWith("Ljava/") ||
                    clazz.getName().toString().startsWith("Lsun/") ||
                    clazz.getName().toString().startsWith("Lcom/sun/") ||
                    clazz.getName().toString().startsWith("Ljavax/")) {
                continue;
            }

            String className = clazz.getName().toString();
            
            // Apply package filtering
            if (!matchesPackageFilter(className, packageFilters)) {
                if (verbose && !packageFilters.isEmpty()) {
                    logger.debug("Filtered out class: {}", className);
                }
                continue;
            }
            
            if (verbose) {
                logger.debug("Processing class: {}", className);
            }

            classes.add(new ClassInfo(className, clazz.isInterface(), clazz.isAbstract()));

            // Collect methods from this class
            for (IMethod method : clazz.getDeclaredMethods()) {
                if (!method.isSynthetic()) {
                    methods.add(new MethodInfo(
                            className,
                            method.getName().toString(),
                            method.getSignature(),
                            method.isStatic(),
                            method.isPrivate(),
                            method.isPublic()
                    ));
                }
            }
        }

        // Collect call edges from call graph
        callGraph.forEach(node -> {
            var method = node.getMethod();
            String callerClass = method.getDeclaringClass().getName().toString();
            String callerMethod = method.getName().toString();

            // Skip system classes in call edges
            if (callerClass.startsWith("Ljava/") ||
                    callerClass.startsWith("Lsun/") ||
                    callerClass.startsWith("Lcom/sun/") ||
                    callerClass.startsWith("Ljavax/")) {
                return;
            }
            
            // Apply package filtering to caller
            if (!matchesPackageFilter(callerClass, packageFilters)) {
                return;
            }

            callGraph.getSuccNodes(node).forEachRemaining(targetNode -> {
                var targetMethod = targetNode.getMethod();
                String targetClass = targetMethod.getDeclaringClass().getName().toString();
                String targetMethodName = targetMethod.getName().toString();

                // Skip system classes in targets too
                if (!targetClass.startsWith("Ljava/") &&
                        !targetClass.startsWith("Lsun/") &&
                        !targetClass.startsWith("Lcom/sun/") &&
                        !targetClass.startsWith("Ljavax/") &&
                        matchesPackageFilter(targetClass, packageFilters)) {

                    callEdges.add(new CallEdgeInfo(
                            callerClass,
                            callerMethod,
                            targetClass,
                            targetMethodName
                    ));

                    if (verbose) {
                        logger.debug("Call edge: {}.{} -> {}.{}",
                                callerClass, callerMethod, targetClass, targetMethodName);
                    }
                }
            });
        });

        return new AnalysisResult(classes, methods, callEdges);
    }

    private boolean matchesPackageFilter(@Nonnull String className, @Nonnull List<String> packageFilters) {
        if (packageFilters.isEmpty()) {
            return true;
        }
        
        // Convert WALA class name format (Lcom/example/Class;) to package format
        String packageName = className;
        if (packageName.startsWith("L") && packageName.endsWith(";")) {
            packageName = packageName.substring(1, packageName.length() - 1);
        }
        packageName = packageName.replace("/", ".");
        
        // Extract package part (remove class name)
        int lastDot = packageName.lastIndexOf(".");
        if (lastDot > 0) {
            packageName = packageName.substring(0, lastDot);
        }
        
        // Check if package matches any filter
        for (String filter : packageFilters) {
            if (packageName.startsWith(filter)) {
                return true;
            }
        }
        
        return false;
    }

    @Nonnull
    private com.ibm.wala.ipa.callgraph.CallGraphBuilder<InstanceKey> createCallGraphBuilder(
            @Nonnull Algorithm algorithm,
            @Nonnull AnalysisOptions options,
            @Nonnull IAnalysisCacheView cache,
            @Nonnull IClassHierarchy classHierarchy
    ) {
        return switch (algorithm) {
            case CHA -> Util.makeZeroCFABuilder(Language.JAVA, options, cache, classHierarchy);
            case RTA -> Util.makeRTABuilder(options, cache, classHierarchy);
            case ZERO_CFA -> Util.makeZeroOneCFABuilder(Language.JAVA, options, cache, classHierarchy);
        };
    }

    public record AnalysisResult(
            @Nonnull List<ClassInfo> classes,
            @Nonnull List<MethodInfo> methods,
            @Nonnull List<CallEdgeInfo> callEdges
    ) {
    }

    public record ClassInfo(
            @Nonnull String name,
            boolean isInterface,
            boolean isAbstract
    ) {
    }

    public record MethodInfo(
            @Nonnull String className,
            @Nonnull String methodName,
            @Nonnull String signature,
            boolean isStatic,
            boolean isPrivate,
            boolean isPublic
    ) {
    }

    public record CallEdgeInfo(
            @Nonnull String callerClass,
            @Nonnull String callerMethod,
            @Nonnull String targetClass,
            @Nonnull String targetMethod
    ) {
    }
}
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

package cherry.callgraph.sootup;

import cherry.callgraph.analyzer.*;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.ClassHierarchyAnalysisAlgorithm;
import sootup.callgraph.RapidTypeAnalysisAlgorithm;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.views.JavaView;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SootUpAnalyzer implements CallGraphAnalyzer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Nonnull
    @Override
    public AnalysisResult analyzeFiles(
            @Nonnull List<String> filePaths,
            boolean verbose,
            @Nonnull List<String> packageFilters,
            @Nonnull List<String> excludeClasses,
            @Nonnull Algorithm algorithm,
            @Nonnull List<String> customEntryPoints,
            boolean excludeJdk
    ) {
        logger.info("Initializing SootUp analysis for {} files", filePaths.size());

        // Create input locations for analysis
        List<AnalysisInputLocation> inputLocations = createInputLocations(filePaths, verbose);

        // Create JavaView with input locations
        JavaView view = createJavaView(inputLocations);

        if (verbose) {
            logger.info("SootUp view created with {} input locations", inputLocations.size());
        }

        // Find entry points
        logger.info("Finding entry points...");
        List<MethodSignature> entryPoints = findEntryPoints(view, verbose, customEntryPoints, packageFilters, excludeClasses);

        // Build call graph using specified algorithm
        logger.info("Building call graph with {}...", algorithm);
        CallGraph callGraph = buildCallGraph(view, entryPoints, algorithm);

        if (verbose) {
            logger.info("Call graph built with {} nodes", callGraph.getMethodSignatures().size());
        }

        // Collect analysis results
        var result = collectAnalysisResults(view, callGraph, verbose, packageFilters, excludeClasses, excludeJdk);

        logger.info("Analysis completed: {} classes, {} methods, {} call edges found",
                result.classes().size(), result.methods().size(), result.callEdges().size());

        return result;
    }

    @Nonnull
    private List<AnalysisInputLocation> createInputLocations(
            @Nonnull List<String> filePaths,
            boolean verbose
    ) {
        return filePaths.stream()
                .map(Paths::get)
                .filter(path -> {
                    if (!Files.exists(path)) {
                        logger.warn("File or directory does not exist: {}", path);
                        return false;
                    }
                    if (verbose) {
                        logger.debug("Adding input location: {}", path);
                    }
                    return true;
                })
                .map(path -> new JavaClassPathAnalysisInputLocation(path.toString()))
                .collect(Collectors.toList());
    }

    @Nonnull
    private JavaView createJavaView(@Nonnull List<AnalysisInputLocation> inputLocations) {
        return new JavaView(inputLocations);
    }

    @Nonnull
    private List<MethodSignature> findEntryPoints(
            @Nonnull JavaView view,
            boolean verbose,
            @Nonnull List<String> customEntryPoints,
            @Nonnull List<String> packageFilters,
            @Nonnull List<String> excludeClasses
    ) {
        List<MethodSignature> entryPoints;

        if (!customEntryPoints.isEmpty()) {
            // Use custom entry points
            entryPoints = customEntryPoints.stream()
                    .flatMap(entryPointSpec -> {
                        var foundMethods = findMethodsBySpec(view, entryPointSpec, packageFilters, excludeClasses);
                        if (verbose) {
                            foundMethods.forEach(method -> logger.info("Found custom entry point: {}", method));
                        }
                        return foundMethods.stream();
                    })
                    .collect(Collectors.toList());
        } else {
            // Find main methods
            entryPoints = view.getClasses()
                    .filter(sootClass -> matchesClassFilter(sootClass.getName(), packageFilters, excludeClasses))
                    .flatMap(sootClass -> sootClass.getMethods().stream())
                    .filter(method -> method.getName().equals("main") &&
                            method.isStatic() &&
                            method.isPublic() &&
                            method.getParameterTypes().size() == 1)
                    .peek(method -> {
                        if (verbose) {
                            logger.info("Found entry point: {}", method.getSignature());
                        }
                    })
                    .map(SootMethod::getSignature)
                    .collect(Collectors.toList());
        }

        if (entryPoints.isEmpty()) {
            if (!customEntryPoints.isEmpty()) {
                logger.warn("No custom entry points found matching: {}", String.join(", ", customEntryPoints));
            } else {
                logger.warn("No main methods found as entry points");
            }
        } else {
            logger.info("Found {} entry point(s)", entryPoints.size());
        }

        return entryPoints;
    }

    @Nonnull
    private List<MethodSignature> findMethodsBySpec(
            @Nonnull JavaView view,
            @Nonnull String entryPointSpec,
            @Nonnull List<String> packageFilters,
            @Nonnull List<String> excludeClasses
    ) {
        // Parse entry point specification: ClassName.methodName or just methodName
        final String className;
        final String methodName;

        if (entryPointSpec.contains(".")) {
            int lastDot = entryPointSpec.lastIndexOf(".");
            className = entryPointSpec.substring(0, lastDot);
            methodName = entryPointSpec.substring(lastDot + 1);
        } else {
            className = null;
            methodName = entryPointSpec;
        }

        return view.getClasses()
                .filter(sootClass -> matchesClassFilter(sootClass.getName(), packageFilters, excludeClasses))
                .filter(sootClass -> {
                    if (className == null) {
                        return true;
                    }
                    String actualClassName = sootClass.getName();
                    return actualClassName.equals(className) || actualClassName.endsWith("." + className);
                })
                .flatMap(sootClass -> sootClass.getMethods().stream())
                .filter(method -> method.getName().equals(methodName))
                .map(SootMethod::getSignature)
                .collect(Collectors.toList());
    }

    @Nonnull
    private CallGraph buildCallGraph(
            @Nonnull JavaView view,
            @Nonnull List<MethodSignature> entryPoints,
            @Nonnull Algorithm algorithm
    ) {
        CallGraphAlgorithm callGraphAlgorithm = switch (algorithm) {
            case CHA -> new ClassHierarchyAnalysisAlgorithm(view);
            case RTA -> new RapidTypeAnalysisAlgorithm(view);
        };

        return callGraphAlgorithm.initialize(entryPoints);
    }

    @Nonnull
    private AnalysisResult collectAnalysisResults(
            @Nonnull JavaView view,
            @Nonnull CallGraph callGraph,
            boolean verbose,
            @Nonnull List<String> packageFilters,
            @Nonnull List<String> excludeClasses,
            boolean excludeJdk
    ) {
        List<ClassInfo> classes = new ArrayList<>();
        List<MethodInfo> methods = new ArrayList<>();
        Set<CallEdgeInfo> callEdgeSet = new LinkedHashSet<>();

        // Collect classes and methods
        view.getClasses()
                .filter(sootClass -> !(excludeJdk && isJdkClass(sootClass.getName())))
                .filter(sootClass -> {
                    boolean matches = matchesClassFilter(sootClass.getName(), packageFilters, excludeClasses);
                    if (verbose && (!packageFilters.isEmpty() || !excludeClasses.isEmpty()) && !matches) {
                        logger.debug("Filtered out class: {}", sootClass.getName());
                    }
                    return matches;
                })
                .peek(sootClass -> {
                    if (verbose) {
                        logger.debug("Processing class: {}", sootClass.getName());
                    }
                })
                .forEach(sootClass -> {
                    String className = sootClass.getName();
                    classes.add(new ClassInfo(className, sootClass.isInterface(), sootClass.isAbstract()));

                    sootClass.getMethods().forEach(method -> methods.add(new MethodInfo(
                            className,
                            method.getName(),
                            method.getSignature().toString(),
                            method.isStatic(),
                            method.isPrivate(),
                            method.isPublic()
                    )));
                });

        // Collect call edges from call graph
        callGraph.getMethodSignatures().forEach(methodSignature ->
                callGraph.callsFrom(methodSignature).stream()
                        .filter(call -> {
                            MethodSignature source = call.getSourceMethodSignature();
                            String sourceClass = source.getDeclClassType().getFullyQualifiedName();
                            return !(excludeJdk && isJdkClass(sourceClass)) &&
                                    matchesClassFilter(sourceClass, packageFilters, excludeClasses);
                        })
                        .filter(call -> {
                            MethodSignature target = call.getTargetMethodSignature();
                            String targetClass = target.getDeclClassType().getFullyQualifiedName();
                            return !(excludeJdk && isJdkClass(targetClass)) &&
                                    matchesClassFilter(targetClass, packageFilters, excludeClasses);
                        })
                        .forEach(call -> {
                            MethodSignature source = call.getSourceMethodSignature();
                            MethodSignature target = call.getTargetMethodSignature();

                            String sourceClass = source.getDeclClassType().getFullyQualifiedName();
                            String sourceMethod = source.getName();
                            String targetClass = target.getDeclClassType().getFullyQualifiedName();
                            String targetMethod = target.getName();

                            callEdgeSet.add(new CallEdgeInfo(
                                    sourceClass,
                                    sourceMethod,
                                    targetClass,
                                    targetMethod
                            ));

                            if (verbose) {
                                logger.debug("Call edge: {}.{} -> {}.{}",
                                        sourceClass, sourceMethod, targetClass, targetMethod);
                            }
                        })
        );

        logger.debug("Total method signatures: {}", callGraph.getMethodSignatures().size());
        List<CallEdgeInfo> callEdges = new ArrayList<>(callEdgeSet);
        logger.debug("Total call edges found: {}", callEdges.size());

        return new AnalysisResult(classes, methods, callEdges);
    }

    private boolean matchesClassFilter(
            @Nonnull String className,
            @Nonnull List<String> packageFilters,
            @Nonnull List<String> excludeClasses
    ) {
        // Check if class matches any exclude filter first (FQCN prefix match)
        for (String excludeFilter : excludeClasses) {
            if (className.startsWith(excludeFilter)) {
                return false;
            }
        }

        // If no package include filters specified, include all (except excluded)
        if (packageFilters.isEmpty()) {
            return true;
        }

        // Extract package part for package filtering
        String packageName = className;
        int lastDot = packageName.lastIndexOf(".");
        if (lastDot > 0) {
            packageName = packageName.substring(0, lastDot);
        } else {
            // If no package (default package), set to empty string
            packageName = "";
        }

        // Check if package matches any include filter
        for (String filter : packageFilters) {
            if (packageName.startsWith(filter)) {
                return true;
            }
        }
        return false;
    }

    private boolean isJdkClass(@Nonnull String className) {
        return className.startsWith("java.") ||
                className.startsWith("sun.") ||
                className.startsWith("com.sun.") ||
                className.startsWith("javax.") ||
                className.startsWith("jdk.") ||
                className.startsWith("com.oracle.") ||
                className.startsWith("org.w3c.") ||
                className.startsWith("org.xml.") ||
                className.startsWith("org.ietf.");
    }
}

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
import sootup.java.core.JavaSootClass;
import sootup.java.core.views.JavaView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SootUpAnalyzer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public enum Algorithm {
        CHA, RTA
    }

    @Nonnull
    public AnalysisResult analyzeFiles(
            @Nonnull List<String> filePaths,
            boolean verbose,
            @Nonnull List<String> packageFilters,
            @Nonnull Algorithm algorithm,
            @Nonnull List<String> customEntryPoints,
            boolean excludeJdk
    ) throws IOException {
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
        List<MethodSignature> entryPoints = findEntryPoints(view, verbose, customEntryPoints, packageFilters);

        // Add interface implementations as additional entry points
        entryPoints = expandEntryPointsWithImplementations(view, entryPoints, packageFilters);

        // Build call graph using specified algorithm
        logger.info("Building call graph with {}...", algorithm);
        CallGraph callGraph = buildCallGraph(view, entryPoints, algorithm);

        if (verbose) {
            logger.info("Call graph built with {} nodes", callGraph.getMethodSignatures().size());
        }

        // Collect analysis results
        var result = collectAnalysisResults(view, callGraph, verbose, packageFilters, excludeJdk);

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
            @Nonnull List<String> packageFilters
    ) {
        List<MethodSignature> entryPoints;

        if (!customEntryPoints.isEmpty()) {
            // Use custom entry points
            entryPoints = customEntryPoints.stream()
                    .flatMap(entryPointSpec -> {
                        var foundMethods = findMethodsBySpec(view, entryPointSpec, packageFilters);
                        if (verbose) {
                            foundMethods.forEach(method -> logger.info("Found custom entry point: {}", method));
                        }
                        return foundMethods.stream();
                    })
                    .collect(Collectors.toList());
        } else {
            // Find main methods
            entryPoints = view.getClasses()
                    .filter(sootClass -> matchesPackageFilter(sootClass.getName(), packageFilters))
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
            @Nonnull List<String> packageFilters
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
                .filter(sootClass -> matchesPackageFilter(sootClass.getName(), packageFilters))
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
    private List<MethodSignature> expandEntryPointsWithImplementations(
            @Nonnull JavaView view,
            @Nonnull List<MethodSignature> originalEntryPoints,
            @Nonnull List<String> packageFilters
    ) {
        List<MethodSignature> expandedEntryPoints = new ArrayList<>(originalEntryPoints);

        // For each original entry point, find interface implementations
        for (MethodSignature entryPoint : originalEntryPoints) {
            view.getClass(entryPoint.getDeclClassType()).ifPresent(entryClass ->
                    addImplementationsForInterfaces(view, entryClass, expandedEntryPoints, packageFilters)
            );
        }

        logger.debug("Expanded to {} total entry points", expandedEntryPoints.size());
        return expandedEntryPoints;
    }

    private void addImplementationsForInterfaces(
            @Nonnull JavaView view,
            @Nonnull JavaSootClass declaringClass,
            @Nonnull List<MethodSignature> entryPoints,
            @Nonnull List<String> packageFilters
    ) {
        // Find interfaces implemented by the declaring class and process with Stream API
        declaringClass.getInterfaces().stream()
                .filter(interfaceType -> matchesPackageFilter(interfaceType.getFullyQualifiedName(), packageFilters))
                .filter(interfaceType -> view.getClass(interfaceType)
                        .filter(JavaSootClass::isInterface).isPresent())
                .flatMap(interfaceType -> view.getClasses()
                        .filter(sootClass -> !sootClass.isInterface() &&
                                !sootClass.isAbstract() &&
                                matchesPackageFilter(sootClass.getName(), packageFilters) &&
                                sootClass.getInterfaces().contains(interfaceType))
                        .flatMap(sootClass -> sootClass.getMethods().stream()
                                .filter(method -> method.isPublic() &&
                                        !method.isAbstract() &&
                                        !method.getName().equals("<init>") &&
                                        !method.getName().equals("<clinit>"))
                                .peek(method -> logger.debug("Added implementation method: {} for interface {}",
                                        method.getSignature(), interfaceType.getFullyQualifiedName()))))
                .forEach(method -> entryPoints.add(method.getSignature()));
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
            boolean excludeJdk
    ) {
        List<ClassInfo> classes = new ArrayList<>();
        List<MethodInfo> methods = new ArrayList<>();
        List<CallEdgeInfo> callEdges = new ArrayList<>();

        // Collect classes and methods
        var allClasses = view.getClasses().collect(Collectors.toList());
        for (JavaSootClass sootClass : allClasses) {
            String className = sootClass.getName();

            // Skip JDK classes if excludeJdk is enabled
            if (excludeJdk && isJdkClass(className)) {
                continue;
            }

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

            classes.add(new ClassInfo(className, sootClass.isInterface(), sootClass.isAbstract()));

            // Collect methods from this class
            for (SootMethod method : sootClass.getMethods()) {
                methods.add(new MethodInfo(
                        className,
                        method.getName(),
                        method.getSignature().toString(),
                        method.isStatic(),
                        method.isPrivate(),
                        method.isPublic()
                ));
            }
        }

        // Collect call edges from call graph
        for (MethodSignature caller : callGraph.getMethodSignatures()) {
            String callerClass = caller.getDeclClassType().getFullyQualifiedName();
            String callerMethod = caller.getName();

            // Skip JDK classes in call edges if excludeJdk is enabled
            if (excludeJdk && isJdkClass(callerClass)) {
                continue;
            }

            // Apply package filtering to caller
            if (!matchesPackageFilter(callerClass, packageFilters)) {
                continue;
            }

            // SootUp 2.0.0 CallGraph API - callsFrom takes single MethodSignature
            callGraph.callsFrom(caller).forEach((CallGraph.Call call) -> {
                MethodSignature target = call.getTargetMethodSignature();
                String targetClass = target.getDeclClassType().getFullyQualifiedName();
                String targetMethod = target.getName();

                // Skip JDK classes in targets if excludeJdk is enabled
                if ((!excludeJdk || !isJdkClass(targetClass)) &&
                        matchesPackageFilter(targetClass, packageFilters)) {

                    callEdges.add(new CallEdgeInfo(
                            callerClass,
                            callerMethod,
                            targetClass,
                            targetMethod
                    ));

                    if (verbose) {
                        logger.debug("Call edge: {}.{} -> {}.{}",
                                callerClass, callerMethod, targetClass, targetMethod);
                    }
                }
            });
        }

        logger.debug("Total method signatures: {}", callGraph.getMethodSignatures().size());
        logger.debug("Total call edges found: {}", callEdges.size());

        return new AnalysisResult(classes, methods, callEdges);
    }

    private boolean matchesPackageFilter(
            @Nonnull String className,
            @Nonnull List<String> packageFilters
    ) {
        if (packageFilters.isEmpty()) {
            return true;
        }

        // Extract package part (remove class name)
        String packageName = className;
        int lastDot = packageName.lastIndexOf(".");
        if (lastDot > 0) {
            packageName = packageName.substring(0, lastDot);
        } else {
            // If no package (default package), set to empty string
            packageName = "";
        }

        // Check if package matches any filter
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
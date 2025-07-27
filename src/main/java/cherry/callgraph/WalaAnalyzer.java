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
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.shrike.shrikeCT.InvalidClassFileException;
import com.ibm.wala.core.util.config.AnalysisScopeReader;
import com.ibm.wala.core.util.io.FileProvider;
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
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;

@Component
public class WalaAnalyzer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Nonnull
    public AnalysisResult analyzeFiles(@Nonnull List<String> filePaths, boolean verbose) throws IOException, ClassHierarchyException {
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

        // Collect classes and methods
        var result = collectClassesAndMethods(classHierarchy, verbose);
        
        logger.info("Analysis completed: {} classes, {} methods found", 
                result.classes().size(), result.methods().size());

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
            Files.walk(path)
                    .filter(Files::isRegularFile)
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
    private AnalysisResult collectClassesAndMethods(
            @Nonnull IClassHierarchy classHierarchy,
            boolean verbose
    ) {
        List<ClassInfo> classes = new ArrayList<>();
        List<MethodInfo> methods = new ArrayList<>();

        Iterator<IClass> classIterator = classHierarchy.iterator();
        while (classIterator.hasNext()) {
            IClass clazz = classIterator.next();
            
            // Skip synthetic and system classes for basic listing
            if (clazz.isInterface() || clazz.isAbstract() || 
                clazz.getName().toString().startsWith("Ljava/") ||
                clazz.getName().toString().startsWith("Lsun/") ||
                clazz.getName().toString().startsWith("Lcom/sun/")) {
                continue;
            }

            String className = clazz.getName().toString();
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

        return new AnalysisResult(classes, methods);
    }

    public record AnalysisResult(
            @Nonnull List<ClassInfo> classes,
            @Nonnull List<MethodInfo> methods
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
}
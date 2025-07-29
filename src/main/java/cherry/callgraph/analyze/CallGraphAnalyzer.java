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

package cherry.callgraph.analyze;

import jakarta.annotation.Nonnull;

import java.util.List;

/**
 * Interface for call graph analysis implementations.
 */
public interface CallGraphAnalyzer {

    /**
     * Analyzes the given files to extract call graph information.
     *
     * @param filePaths         list of file paths to analyze
     * @param verbose           whether to output verbose information
     * @param packageFilters    list of package filters to include
     * @param excludeClasses    list of class prefixes to exclude
     * @param algorithm         analysis algorithm to use
     * @param customEntryPoints list of custom entry points
     * @param excludeJdk        whether to exclude JDK classes
     * @return analysis result containing classes, methods, and call edges
     */
    @Nonnull
    AnalysisResult analyzeFiles(
            @Nonnull List<String> filePaths,
            boolean verbose,
            @Nonnull List<String> packageFilters,
            @Nonnull List<String> excludeClasses,
            @Nonnull Algorithm algorithm,
            @Nonnull List<String> customEntryPoints,
            boolean excludeJdk
    );
}

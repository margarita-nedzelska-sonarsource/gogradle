/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.blindpirate.gogradle.core.dependency.produce;

import com.github.blindpirate.gogradle.core.GolangPackage;
import com.github.blindpirate.gogradle.core.ResolvableGolangPackage;
import com.github.blindpirate.gogradle.core.StandardGolangPackage;
import com.github.blindpirate.gogradle.core.UnrecognizedGolangPackage;
import com.github.blindpirate.gogradle.core.dependency.GolangDependency;
import com.github.blindpirate.gogradle.core.dependency.GolangDependencySet;
import com.github.blindpirate.gogradle.core.dependency.ResolvedDependency;
import com.github.blindpirate.gogradle.core.dependency.parse.NotationParser;
import com.github.blindpirate.gogradle.core.pack.PackagePathResolver;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.blindpirate.gogradle.util.StringUtils.isBlank;

/**
 * Scans all .go code to generate dependencies.
 */
@Singleton
public class SourceCodeDependencyFactory {

    public static final String TESTDATA_DIRECTORY = "testdata";

    private final GoImportExtractor goImportExtractor;
    private final PackagePathResolver packagePathResolver;
    private final NotationParser notationParser;

    @Inject
    public SourceCodeDependencyFactory(PackagePathResolver packagePathResolver,
                                       NotationParser notationParser,
                                       GoImportExtractor goImportExtractor) {
        this.packagePathResolver = packagePathResolver;
        this.notationParser = notationParser;
        this.goImportExtractor = goImportExtractor;
    }

    public GolangDependencySet produce(ResolvedDependency resolvedDependency,
                                       File rootDir,
                                       String configuration) {
        return createDependencies(resolvedDependency, goImportExtractor.getImportPaths(rootDir, configuration));
    }

    private GolangDependencySet createDependencies(ResolvedDependency resolvedDependency, Set<String> importPaths) {
        Set<String> rootPackagePaths =
                importPaths.stream()
                        .filter(path -> !path.startsWith(resolvedDependency.getName()))
                        .map(this::getRootPath)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());

        Set<GolangDependency> dependencies =
                rootPackagePaths.stream()
                        .<GolangDependency>map(notationParser::parse)
                        .collect(Collectors.toSet());

        return new GolangDependencySet(dependencies);

    }

    private Optional<String> getRootPath(String importPath) {
        if (isBlank(importPath)) {
            return Optional.empty();
        }
        if (isRelativePath(importPath)) {
            return Optional.empty();
        }

        GolangPackage info = packagePathResolver.produce(importPath).get();
        if (info instanceof StandardGolangPackage) {
            return Optional.empty();
        }

        if (info instanceof UnrecognizedGolangPackage) {
            return Optional.of(importPath);
        }

        String rootPath = ResolvableGolangPackage.class.cast(info).getRootPathString();

        return Optional.of(rootPath);
    }

    private boolean isRelativePath(String importPath) {
        return importPath.startsWith(".");
    }


}

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

package com.github.blindpirate.gogradle.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class UnrecognizedGolangPackage extends GolangPackage {

    private UnrecognizedGolangPackage(Path path) {
        super(path);
    }

    @Override
    protected Optional<GolangPackage> longerPath(Path packagePath) {
        // I cannot foresee the future
        // for example, I am `golang.org` and I am unrecognized
        // but `golang.org/x/tools` can be recognized
        return Optional.empty();
    }

    @Override
    protected Optional<GolangPackage> shorterPath(Path packagePath) {
        return Optional.of(of(packagePath));
    }

    public static UnrecognizedGolangPackage of(Path packagePath) {
        return new UnrecognizedGolangPackage(packagePath);
    }

    public static UnrecognizedGolangPackage of(String packagePath) {
        return of(Paths.get(packagePath));
    }

    @Override
    public String toString() {
        return "UnrecognizedGolangPackage{"
                + "path='" + getPathString() + '\''
                + '}';
    }
}

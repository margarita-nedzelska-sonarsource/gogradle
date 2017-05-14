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

package com.github.blindpirate.gogradle.vcs.svn;

import com.github.blindpirate.gogradle.core.cache.CacheScope;
import com.github.blindpirate.gogradle.core.dependency.AbstractNotationDependency;
import com.github.blindpirate.gogradle.core.dependency.ResolveContext;
import com.github.blindpirate.gogradle.core.dependency.ResolvedDependency;

public class SvnNotationDependency extends AbstractNotationDependency {
    public SvnNotationDependency() {
        throw new UnsupportedOperationException("Svn is not implemented yet!");
    }

    @Override
    protected ResolvedDependency doResolve(ResolveContext context) {
        throw new UnsupportedOperationException("Svn is not implemented yet!");
    }

    @Override
    public CacheScope getCacheScope() {
        throw new UnsupportedOperationException("Svn is not implemented yet!");
    }
}

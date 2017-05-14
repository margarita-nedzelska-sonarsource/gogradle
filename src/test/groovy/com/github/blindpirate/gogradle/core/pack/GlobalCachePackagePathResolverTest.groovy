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

package com.github.blindpirate.gogradle.core.pack

import com.github.blindpirate.gogradle.GogradleRunner
import com.github.blindpirate.gogradle.core.GolangPackage
import com.github.blindpirate.gogradle.core.VcsGolangPackage
import com.github.blindpirate.gogradle.core.cache.GlobalCacheManager
import com.github.blindpirate.gogradle.core.cache.GlobalCacheMetadata
import com.github.blindpirate.gogradle.support.MockRefreshDependencies
import com.github.blindpirate.gogradle.util.MockUtils
import com.github.blindpirate.gogradle.vcs.VcsType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

import java.nio.file.Paths

import static java.util.Optional.of
import static org.mockito.Mockito.when

@RunWith(GogradleRunner)
@MockRefreshDependencies(false)
class GlobalCachePackagePathResolverTest {
    @Mock
    GlobalCacheManager cacheManager

    GlobalCachePackagePathResolver resolver

    File resource

    @Before
    void setUp() {
        resolver = new GlobalCachePackagePathResolver(cacheManager)
    }

    @Test
    void 'resolving metadata in global cache should succeed'() {
        // when
        VcsGolangPackage pkg = MockUtils.mockVcsPackage()
        GlobalCacheMetadata metadata = GlobalCacheMetadata.newMetadata(pkg)
        when(cacheManager.getMetadata(Paths.get('github.com/user/package'))).thenReturn(of(metadata))
        GolangPackage info = resolver.produce('github.com/user/package/a/b').get()

        // then
        assert info.vcsType == VcsType.GIT
        assert info.pathString == 'github.com/user/package/a/b'
        assert info.rootPathString == 'github.com/user/package'
        assert info.urls == ['git@github.com:user/package.git', 'https://github.com/user/package.git']
    }

    @Test
    void 'empty result should be returned if all subpaths are not found'() {
        assert !resolver.produce('inexistent').isPresent()
    }

}

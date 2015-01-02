/*
 * Copyright 2015 Vincent Oostindie
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
 * limitations under the License
 */

package nl.ulso.magisto;

import nl.ulso.magisto.git.DummyGitClient;
import nl.ulso.magisto.io.RealFileSystem;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;

public class RealMagistoFactoryBuilderTest {

    @Test
    public void testFactory() throws Exception {
        final MagistoFactory factory = new RealMagistoFactoryBuilder(new RealFileSystem(), new DummyGitClient())
                .withSourceRoot(Paths.get(".").toAbsolutePath().resolve("source"))
                .withTargetRoot(Paths.get(".").toAbsolutePath().resolve("target"))
                .build();
        assertNotNull(factory);
    }

    @Test(expected = IllegalStateException.class)
    public void testFactoryWithOverlappingPaths() throws Exception {
        final MagistoFactory factory = new RealMagistoFactoryBuilder(new RealFileSystem(), new DummyGitClient())
                .withSourceRoot(Paths.get("."))
                .withTargetRoot(Paths.get("."))
                .build();
        assertNotNull(factory);
    }
}
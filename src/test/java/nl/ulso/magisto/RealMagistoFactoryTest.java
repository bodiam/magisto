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
import nl.ulso.magisto.io.DummyFileSystem;
import org.junit.Before;
import org.junit.Test;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertNotNull;

public class RealMagistoFactoryTest {

    private MagistoFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new RealMagistoFactory(new DummyFileSystem(), new DummyGitClient(),
                createPath("source").toAbsolutePath(), createPath("target").toAbsolutePath());
    }

    @Test
    public void testCreateDocumentLoader() throws Exception {
        assertNotNull(factory.createDocumentLoader());
    }

    @Test
    public void testCreateDocumentConverter() throws Exception {
        assertNotNull(factory.createDocumentConverter());
    }

    @Test
    public void testCreateActionFactory() throws Exception {
        assertNotNull(factory.createActionFactory());
    }

    @Test
    public void testCreateSitemap() throws Exception {
        assertNotNull(factory.createSitemap());
    }
}
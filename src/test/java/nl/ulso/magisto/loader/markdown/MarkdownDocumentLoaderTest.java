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

package nl.ulso.magisto.loader.markdown;

import nl.ulso.magisto.document.Document;
import nl.ulso.magisto.git.DummyGitClient;
import nl.ulso.magisto.io.DummyFileSystem;
import org.junit.Before;
import org.junit.Test;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.*;

public class MarkdownDocumentLoaderTest {

    private DummyFileSystem fileSystem;
    private MarkdownDocumentLoader loader;

    @Before
    public void setUp() throws Exception {
        fileSystem = new DummyFileSystem();
        final DummyGitClient gitClient = new DummyGitClient();
        loader = new MarkdownDocumentLoader(fileSystem, fileSystem.getSourceRoot(), gitClient);
    }

    @Test
    public void testMarkdownExtensionMd() throws Exception {
        assertTrue(loader.supports(createPath("foo.md")));
    }

    @Test
    public void testMarkdownExtensionMdown() throws Exception {
        assertTrue(loader.supports(createPath("foo.mdown")));
    }

    @Test
    public void testMarkdownExtensionMarkdown() throws Exception {
        assertTrue(loader.supports(createPath("foo.markdown")));
    }

    @Test
    public void testMarkdownExtensionMarkdownWeirdCasing() throws Exception {
        assertTrue(loader.supports(createPath("foo.MarkDown")));
    }

    @Test
    public void testNormalFile() throws Exception {
        assertFalse(loader.supports(createPath("foo.jpg")));
    }

    @Test
    public void testLoadDocument() throws Exception {
        fileSystem.registerTextFileForBufferedReader("test.md", "# Test\n\nThis is a test");
        final Document document = loader.loadDocument(createPath("test.md"));
        assertEquals("Test", document.getTitle());
    }
}

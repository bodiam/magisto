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

package nl.ulso.magisto.io;

import org.junit.Test;

import java.nio.file.Path;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PathsTest {

    @Test
    public void testAbsoluteOk() throws Exception {
        assertNotNull(Paths.requireAbsolutePath(createPath(System.getProperty("user.dir"))));
    }

    @Test(expected = IllegalStateException.class)
    public void testAbsoluteNotOk() throws Exception {
        assertNotNull(Paths.requireAbsolutePath(createPath("src")));
    }

    @Test
    public void testRelativeOk() throws Exception {
        assertNotNull(Paths.requireRelativePath(createPath("src")));
    }

    @Test(expected = IllegalStateException.class)
    public void testRelativeNotOk() throws Exception {
        assertNotNull(Paths.requireRelativePath(createPath(System.getProperty("user.dir"))));
    }

    @Test
    public void testExtensionLessPath() throws Exception {
        final Path path = Paths.createPath("file.txt");
        final SplitPath splitPath = Paths.splitOnExtension(path);
        assertEquals("file", splitPath.getPathWithoutExtension().toString());
        assertEquals("txt", splitPath.getOriginalExtension());
    }

    @Test
    public void testExtensionLessPathInSubdirectory() throws Exception {
        final Path path = Paths.createPath("path/to/file.txt");
        final SplitPath splitPath = Paths.splitOnExtension(path);
        assertEquals("path/to/file", splitPath.getPathWithoutExtension().toString());
        assertEquals("txt", splitPath.getOriginalExtension());
    }

    @Test
    public void testExtensionLessPathOnExtensionLessFile() throws Exception {
        final Path path = Paths.createPath("file");
        final SplitPath splitPath = Paths.splitOnExtension(path);
        assertEquals("file", splitPath.getPathWithoutExtension().toString());
        assertEquals("", splitPath.getOriginalExtension());
    }
}
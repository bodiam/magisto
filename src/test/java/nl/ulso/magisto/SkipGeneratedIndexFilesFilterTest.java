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

import nl.ulso.magisto.converter.DummyDocumentConverter;
import nl.ulso.magisto.io.FileSystemTestRunner;
import nl.ulso.magisto.io.FileSystemTestWithPreparedDirectory;
import nl.ulso.magisto.io.RealFileSystem;
import nl.ulso.magisto.loader.DummyDocumentLoader;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SkipGeneratedIndexFilesFilterTest {

    @Test
    public void testFilterWithoutCustomIndexes() throws Exception {
        final SkipGeneratedIndexFilesFilter filter = new SkipGeneratedIndexFilesFilter(Collections.<Path>emptySet(),
                new DummyDocumentLoader(), new DummyDocumentConverter());
        FileSystemTestRunner.runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("foo.converted"));
                Files.createFile(path.resolve("index.converted")); // Must be skipped
            }

            @Override
            public void runTest(Path path) throws IOException {
                final SortedSet<Path> paths = new RealFileSystem().findAllPaths(path, filter);
                assertThat(paths.size(), is(1));
                assertThat(paths.first().getFileName().toString(), is("foo.converted"));
            }
        });
    }

    @Test
    public void testFilterWithCustomIndexes() throws Exception {
        final Set<Path> sourcePaths = new HashSet<>();
        sourcePaths.add(createPath("index.convert"));
        sourcePaths.add(createPath("foo.convert"));
        sourcePaths.add(createPath("bar.jpg"));
        final SkipGeneratedIndexFilesFilter filter = new SkipGeneratedIndexFilesFilter(sourcePaths,
                new DummyDocumentLoader(), new DummyDocumentConverter());
        FileSystemTestRunner.runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("foo.converted"));
                Files.createFile(path.resolve("index.converted")); // Must NOT be skipped now
            }

            @Override
            public void runTest(Path path) throws IOException {
                final SortedSet<Path> paths = new RealFileSystem().findAllPaths(path, filter);
                assertThat(paths.size(), is(2));
                assertThat(paths.first().getFileName().toString(), is("foo.converted"));
            }
        });
    }

    @Test
    public void testFilterWithIndexFileInSource() throws Exception {
        final Set<Path> sourcePaths = new HashSet<>();
        sourcePaths.add(createPath("index.converted"));
        final SkipGeneratedIndexFilesFilter filter = new SkipGeneratedIndexFilesFilter(sourcePaths,
                new DummyDocumentLoader(), new DummyDocumentConverter());
        FileSystemTestRunner.runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("index.converted")); // Must NOT be skipped now
            }

            @Override
            public void runTest(Path path) throws IOException {
                final SortedSet<Path> paths = new RealFileSystem().findAllPaths(path, filter);
                assertThat(paths.size(), is(1));
                assertThat(paths.first().getFileName().toString(), is("index.converted"));
            }
        });
    }
}
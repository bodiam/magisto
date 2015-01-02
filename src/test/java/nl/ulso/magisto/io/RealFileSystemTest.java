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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.SortedSet;

import static nl.ulso.magisto.io.FileSystemTestRunner.WORKING_DIRECTORY;
import static nl.ulso.magisto.io.FileSystemTestRunner.runFileSystemTest;
import static nl.ulso.magisto.io.Paths.createPath;
import static nl.ulso.magisto.io.Paths.requireRelativePath;
import static org.junit.Assert.*;

public class RealFileSystemTest {

    private final FileSystem fileSystem = new RealFileSystem();

    @Test
    public void testFindAllPaths() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("foo"));
                Files.createDirectory(path.resolve("bar"));
                Files.createFile(path.resolve("bar").resolve("baz"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                final SortedSet<Path> paths = fileSystem.findAllPaths(path);
                assertEquals(3, paths.size());
                assertArrayEquals(new Path[]{
                        createPath("bar"),
                        createPath("bar", "baz"),
                        createPath("foo")}, paths.toArray());
            }
        });
    }

    @Test
    public void testFindAllPathsWithDirectoryFilter() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("foo"));
                Files.createDirectory(path.resolve("bar"));
                Files.createFile(path.resolve("bar").resolve("baz"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                final SortedSet<Path> paths = fileSystem.findAllPaths(path, new PathFilter() {
                    @Override
                    public boolean acceptDirectory(Path path) {
                        requireRelativePath(path);
                        return !path.getFileName().toString().equals("bar");
                    }

                    @Override
                    public boolean acceptFile(Path path) {
                        requireRelativePath(path);
                        return true;
                    }
                });
                assertEquals(1, paths.size());
                assertArrayEquals(new Path[]{createPath("foo")}, paths.toArray());
            }
        });
    }

    @Test
    public void testFindAllPathsWithFileFilter() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("foo"));
                Files.createDirectory(path.resolve("bar"));
                Files.createFile(path.resolve("bar").resolve("baz"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                final SortedSet<Path> paths = fileSystem.findAllPaths(path, new PathFilter() {
                    @Override
                    public boolean acceptDirectory(Path path) {
                        return true;
                    }

                    @Override
                    public boolean acceptFile(Path path) {
                        return path.getFileName().toString().equals("foo");
                    }
                });
                assertEquals(2, paths.size());
                assertArrayEquals(new Path[]{
                        createPath("bar"),
                        createPath("foo")}, paths.toArray());
            }
        });
    }

    @Test
    public void testLastModified() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {

            private long now;

            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                now = System.currentTimeMillis() / 1000; // At least on OS X granularity is at seconds.
                Files.createFile(path.resolve("target"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                final long lastModifiedInMillis = fileSystem.getLastModifiedInMillis(path.resolve("target"));
                System.out.println("now = " + now);
                System.out.println("lastModifiedInMillis = " + lastModifiedInMillis);
                assertTrue(lastModifiedInMillis / 1000 >= now);
            }
        });
    }

    @Test
    public void testHiddenFilesAreSkipped() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createDirectory(path.resolve(".tmpdir"));
                Files.createFile(path.resolve(".tmpdir").resolve("file"));
                Files.createFile(path.resolve(".tmpfile"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                assertEquals(0, fileSystem.findAllPaths(path).size());
            }
        });
    }

    @Test
    public void testPathCopy() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {

            private Path source;
            private Path target;

            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                this.source = path.resolve("source");
                this.target = path.resolve("target");
                Files.createDirectory(source);
                Files.createFile(source.resolve("file"));
                Files.createDirectory(source.resolve("directory"));
                Files.createFile(source.resolve("directory").resolve("file"));
                Files.createDirectory(target);
            }

            @Override
            public void runTest(Path path) throws IOException {
                fileSystem.copy(source, target, createPath("file"));
                fileSystem.copy(source, target, createPath("directory"));
                fileSystem.copy(source, target, createPath("directory").resolve("file"));
                // Bad test, it depends on code that's under test itself:
                assertEquals(3, fileSystem.findAllPaths(path.resolve("target")).size());
            }
        });
    }

    @Test
    public void testPathDelete() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("file"));
                Files.createDirectory(path.resolve("directory"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                fileSystem.delete(path.resolve("file"));
                fileSystem.delete(path.resolve("directory"));
                // Bad test, it depends on code that's under test itself:
                assertEquals(0, fileSystem.findAllPaths(path).size());
            }
        });
    }

    @Test
    public void testBufferedReaderForTextFile() throws Exception {
        final Path textFile = WORKING_DIRECTORY.resolve("README.md");
        final String line;
        try (final BufferedReader bufferedReader = fileSystem.newBufferedReaderForTextFile(textFile)) {
            line = bufferedReader.readLine();
        }
        assertEquals("# Magisto", line);
    }

    @Test
    public void testBufferedWriterForTextFile() throws Exception {
        runFileSystemTest(new FileSystemTestWithEmptyTempDirectory() {
            @Override
            public void runTest(Path path) throws IOException {
                final Path textFile = path.resolve("test.md");
                try (final BufferedWriter writer = fileSystem.newBufferedWriterForTextFile(textFile)) {
                    writer.write("# Test");
                }
                assertTrue(Files.exists(textFile));
                final List<String> strings = Files.readAllLines(textFile, Charset.forName("UTF-8"));
                assertEquals(1, strings.size());
                assertEquals("# Test", strings.get(0));
            }
        });
    }


    @Test
    public void testDirectoryIsEmpty() throws Exception {
        runFileSystemTest(new FileSystemTestWithEmptyTempDirectory() {
            @Override
            public void runTest(Path path) throws IOException {
                assertTrue(fileSystem.isEmpty(path));
            }
        });
    }

    @Test
    public void testDirectoryIsNotEmpty() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("foo"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                assertFalse(fileSystem.isEmpty(path));
            }
        });
    }
}
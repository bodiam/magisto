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
import nl.ulso.magisto.io.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;

import static nl.ulso.magisto.io.FileSystemTestRunner.WORKING_DIRECTORY;
import static nl.ulso.magisto.io.FileSystemTestRunner.runFileSystemTest;
import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RealMagistoFactoryBuilderTest {

    private RealMagistoFactoryBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new RealMagistoFactoryBuilder(new RealFileSystem(), new DummyGitClient());
    }

    @Test
    public void testSourceDirectoryWithJavaCurrentWorkingDirectory() throws Exception {
        final Path expected = WORKING_DIRECTORY;
        final Path actual = ((RealMagistoFactoryBuilder) builder.withSourceDirectory(System.getProperty("user.dir"))).getSourceRoot();
        assertEquals(expected, actual);
    }

    @Test(expected = NoSuchFileException.class)
    public void testSourceDirectoryExceptionForNonExistingDirectory() throws Exception {
        builder.withSourceDirectory("foo");
    }

    @Test
    public void testResolvedSourceDirectoryIsRealPath() throws Exception {
        final Path expected = WORKING_DIRECTORY.resolve("src");
        final Path actual = ((RealMagistoFactoryBuilder) builder.withSourceDirectory(
                createPath("src").toAbsolutePath().toString())).getSourceRoot();
        assertEquals(expected.toString(), actual.toString());
    }

    @Test(expected = IOException.class)
    public void testSourceDirectoryIsDirectory() throws Exception {
        builder.withSourceDirectory("pom.xml");
    }

    @Test(expected = IOException.class)
    public void testSourceDirectoryIsReadable() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                // Make the directory unreadable
                Files.setPosixFilePermissions(path, Collections.<PosixFilePermission>emptySet());
            }

            @Override
            public void runTest(Path path) throws IOException {
                // IOException expected, since directory is not readable
                builder.withSourceDirectory(path.toString());
            }
        });
    }

    @Test
    public void testTargetDirectoryWithNonExistingDirectoryIsRealPath() throws Exception {
        runFileSystemTest(new FileSystemTestWithoutTempDirectory() {
            @Override
            public void runTest(Path path) throws IOException {
                final Path actual = ((RealMagistoFactoryBuilder) builder.withTargetDirectory(path.toString())).getTargetRoot();
                assertEquals(path.toString(), actual.toString());
            }
        });
    }

    @Test
    public void testTargetDirectoryWithExistingDirectoryIsRealPath() throws Exception {
        runFileSystemTest(new FileSystemTestWithEmptyTempDirectory() {
            @Override
            public void runTest(Path path) throws IOException {
                final Path actual = ((RealMagistoFactoryBuilder) builder.withTargetDirectory(path.toString())).getTargetRoot();
                assertEquals(path.toString(), actual.toString());
            }
        });
    }

    @Test(expected = IOException.class)
    public void testTargetDirectoryIsDirectory() throws Exception {
        builder.withTargetDirectory("pom.xml");
    }

    @Test(expected = IOException.class)
    public void testTargetDirectoryIsWritable() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.setPosixFilePermissions(path, Collections.<PosixFilePermission>emptySet());
            }

            @Override
            public void runTest(Path path) throws IOException {
                builder.withTargetDirectory(path.toString());
            }
        });
    }

    @Test(expected = IOException.class)
    public void testTargetDirectoryMustBeEmpty() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("foo"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                builder.withTargetDirectory(path.toString());
            }
        });
    }

    @Test
    public void testTargetDirectoryMustBeEmptyUnlessItIsAnExport() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createFile(path.resolve("foo"));
                Files.createFile(path.resolve(TouchFile.TOUCH_FILE));
            }

            @Override
            public void runTest(Path path) throws IOException {
                assertNotNull(((RealMagistoFactoryBuilder) builder.withTargetDirectory(path.toString())).getTargetRoot());
            }
        });
    }

    @Test(expected = IOException.class)
    public void testTargetDirectoryContainsDirectory() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                Files.createDirectory(path.resolve("foo"));
            }

            @Override
            public void runTest(Path path) throws IOException {
                assertNotNull(((RealMagistoFactoryBuilder) builder.withTargetDirectory(path.toString())).getTargetRoot());
            }
        });
    }

    @Test
    public void testFactory() throws Exception {
        final DummyFileSystem fileSystem = new DummyFileSystem();
        builder = new RealMagistoFactoryBuilder(fileSystem, new DummyGitClient());
        final MagistoFactory factory = builder
                .withSourceDirectory(fileSystem.getSourceRoot().toString())
                .withTargetDirectory(fileSystem.getTargetRoot().toString())
                .build();
        assertNotNull(factory);
    }

    @Test(expected = IllegalStateException.class)
    public void testFactoryWithOverlappingPaths() throws Exception {
        final DummyFileSystem fileSystem = new DummyFileSystem();
        builder = new RealMagistoFactoryBuilder(fileSystem, new DummyGitClient());
        final MagistoFactory factory = builder
                .withSourceDirectory(fileSystem.getSourceRoot().toString())
                .withTargetDirectory(fileSystem.getSourceRoot().toString())
                .build();
        assertNotNull(factory);
    }
}
/*
 * Copyright 2014 Vincent Oostindie
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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import static nl.ulso.magisto.io.Paths.createPath;

/**
 * Runs tests on the filesystem, preparing a temporary test directory when needed, and removing it afterwards.
 */
public class FileSystemTestRunner {

    public static final Path WORKING_DIRECTORY;
    static {
        try {
            // The "toRealPath" ensures that capitalization is correct
            // "/Users/vincent/Code" (real path) versus "/Users/vincent/code" (user.dir))
            WORKING_DIRECTORY = createPath(System.getProperty("user.dir")).toRealPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void runFileSystemTest(FileSystemTest test) throws IOException {
        final Path path = resolveTempDirectory();
        createTempDirectoryIfNeeded(test, path);
        try {
            test.runTest(path);
        } finally {
            cleanupTempDirectory(path);
        }
    }

    private static Path resolveTempDirectory() {
        return WORKING_DIRECTORY.resolve("target").resolve("magisto");
    }

    private static void createTempDirectoryIfNeeded(FileSystemTest test, Path path) {
        if (test.mustCreateTempDirectory()) {
            try {
                Files.createDirectory(path);
                test.prepareTempDirectory(path);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static void cleanupTempDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            fixTempDirectoryPermissions(path);
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path directory, IOException exc) throws IOException {
                        Files.delete(directory);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void fixTempDirectoryPermissions(Path path) throws IOException {
        final Set<PosixFilePermission> permissions = new HashSet<>();
        permissions.add(PosixFilePermission.OWNER_READ);
        permissions.add(PosixFilePermission.OWNER_WRITE);
        permissions.add(PosixFilePermission.OWNER_EXECUTE);
        Files.setPosixFilePermissions(path, permissions);
    }
}

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardOpenOption.*;
import static nl.ulso.magisto.io.Paths.*;

/**
 * Default implementation of the {@link FileSystem} that actually accesses the file system.
 */
public class RealFileSystem implements FileSystem {

    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    @Override
    public Path resolveSourceDirectory(String directoryName) throws IOException {
        final Path path = createPath(directoryName);
        if (Files.notExists(path)) {
            throw new NoSuchFileException(path.toString());
        }
        if (!Files.isDirectory(path)) {
            throw new IOException("Not a directory: " + path);
        }
        if (!Files.isReadable(path)) {
            throw new IOException("Directory not readable: " + path);
        }
        return path.toRealPath();
    }

    @Override
    public Path prepareTargetDirectory(String directoryName) throws IOException {
        final Path path = createPath(directoryName);
        if (Files.notExists(path)) {
            return Files.createDirectories(path);
        }
        if (!Files.isDirectory(path)) {
            throw new IOException("Not a directory: " + path);
        }
        if (!Files.isWritable(path)) {
            throw new IOException("Directory not writable: " + path);
        }
        final TargetStatus status = new TargetStatus();
        Files.walkFileTree(path, Collections.<FileVisitOption>emptySet(), 1, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                if (MAGISTO_EXPORT_MARKER_FILE.equals(file.getFileName().toString())) {
                    status.isExport = true;
                    return FileVisitResult.TERMINATE;
                }
                status.hasFiles = true;
                return FileVisitResult.CONTINUE;
            }
        });
        if (status.hasFiles && !status.isExport) {
            throw new IOException("Directory not empty and not an export: " + path);
        }
        return path.toRealPath();
    }

    @Override
    public void requireDistinct(Path sourceRoot, Path targetRoot) throws IOException {
        requireAbsolutePath(sourceRoot);
        requireAbsolutePath(targetRoot);
        if (targetRoot.startsWith(sourceRoot)) {
            throw new IOException("The target directory may not be inside the source directory");
        }
        if (sourceRoot.startsWith(targetRoot)) {
            throw new IOException("The source directory may not be inside the target directory");
        }
    }

    @Override
    public void writeTouchFile(Path targetRoot) throws IOException {
        final Path touchFile = requireAbsolutePath(targetRoot).resolve(MAGISTO_EXPORT_MARKER_FILE);
        if (Files.exists(touchFile)) {
            Files.delete(touchFile);
        }
        Files.createFile(touchFile);
    }

    @Override
    public long getTouchFileLastModifiedInMillis(Path targetRoot) throws IOException {
        final Path touchFile = requireAbsolutePath(targetRoot).resolve(MAGISTO_EXPORT_MARKER_FILE);
        if (Files.notExists(touchFile)) {
            return -1;
        }
        return Files.getLastModifiedTime(touchFile).toMillis();
    }

    @Override
    public SortedSet<Path> findAllPaths(Path root) throws IOException {
        return findAllPaths(root, DEFAULT_PATH_FILTER, DEFAULT_PATH_COMPARATOR);
    }

    @Override
    public SortedSet<Path> findAllPaths(final Path root, Comparator<? super Path> comparator) throws IOException {
        return findAllPaths(root, DEFAULT_PATH_FILTER, comparator);
    }

    @Override
    public SortedSet<Path> findAllPaths(Path root, PathFilter filter) throws IOException {
        return findAllPaths(root, filter, DEFAULT_PATH_COMPARATOR);
    }

    @Override
    public SortedSet<Path> findAllPaths(final Path root, final PathFilter filter, Comparator<? super Path> comparator)
            throws IOException {
        final SortedSet<Path> paths = new TreeSet<>(comparator);
        Files.walkFileTree(requireAbsolutePath(root), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attributes) throws IOException {
                if (root != path && Files.isHidden(path)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                final Path relativePath = root.relativize(path);
                if (!filter.acceptDirectory(relativePath)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                if (root != path) {
                    paths.add(relativePath);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
                final Path relativePath = root.relativize(path);
                if (!Files.isHidden(path) && filter.acceptFile(relativePath)) {
                    paths.add(relativePath);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return paths;
    }

    @Override
    public long getLastModifiedInMillis(Path path) throws IOException {
        requireAbsolutePath(path);
        return Files.getLastModifiedTime(path).toMillis();
    }

    @Override
    public void copy(Path sourceRoot, Path targetRoot, Path path) throws IOException {
        requireAbsolutePath(sourceRoot);
        requireAbsolutePath(targetRoot);
        requireRelativePath(path);
        Logger.getGlobal().log(Level.FINE,
                String.format("Copying '%s' from '%s' to '%s'.", path, sourceRoot, targetRoot));
        final Path source = sourceRoot.resolve(path);
        final Path target = targetRoot.resolve(path);
        if (Files.isDirectory(source)) {
            if (Files.notExists(target)) {
                Files.createDirectory(target);
            }
        } else {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        }
    }

    @Override
    public void delete(Path root, Path path) throws IOException {
        requireAbsolutePath(root);
        requireRelativePath(path);
        Logger.getGlobal().log(Level.FINE, String.format("Deleting '%s' from '%s'.", path, root));
        Files.delete(root.resolve(path));
    }

    @Override
    public BufferedReader newBufferedReaderForTextFile(Path path) throws IOException {
        return Files.newBufferedReader(path, CHARSET_UTF8);
    }

    @Override
    public BufferedWriter newBufferedWriterForTextFile(Path path) throws IOException {
        return Files.newBufferedWriter(path, CHARSET_UTF8, CREATE, WRITE, TRUNCATE_EXISTING);
    }

    @Override
    public boolean exists(Path path) {
        return Files.exists(path);
    }

    @Override
    public boolean notExists(Path path) {
        return Files.notExists(path);
    }

    private static class TargetStatus {
        boolean isExport = false;
        boolean hasFiles = false;
    }
}

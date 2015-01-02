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
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardOpenOption.*;
import static nl.ulso.magisto.io.Paths.requireAbsolutePath;
import static nl.ulso.magisto.io.Paths.requireRelativePath;

/**
 * Default implementation of the {@link FileSystem} that actually accesses the file system.
 */
public class RealFileSystem implements FileSystem {

    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

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
                if (root != path && !filter.acceptDirectory(relativePath)) {
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
    public boolean exists(Path path) {
        return Files.exists(path);
    }

    @Override
    public boolean notExists(Path path) {
        return Files.notExists(path);
    }

    @Override
    public boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }

    @Override
    public boolean isReadable(Path path) {
        return Files.isReadable(path);
    }

    @Override
    public boolean isWritable(Path path) {
        return Files.isWritable(path);
    }

    @Override
    public boolean isEmpty(Path directory) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            // Silly code here, but stream.iterator().hasNext() doesn't seem to work.
            for (Path path : stream) {
                return false;
            }
            return true;
        }
    }

    @Override
    public Path toRealPath(Path path) throws IOException {
        requireAbsolutePath(path);
        return path.toRealPath();
    }

    @Override
    public void createFile(Path file) throws IOException {
        requireAbsolutePath(file);
        Files.createFile(file);
    }

    @Override
    public void createDirectories(Path path) throws IOException {
        Files.createDirectories(path);
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
    public void delete(Path path) throws IOException {
        requireAbsolutePath(path);
        Logger.getGlobal().log(Level.FINE, String.format("Deleting '%s'.", path));
        Files.delete(path);
    }

    @Override
    public BufferedReader newBufferedReaderForTextFile(Path path) throws IOException {
        return Files.newBufferedReader(path, CHARSET_UTF8);
    }

    @Override
    public BufferedWriter newBufferedWriterForTextFile(Path path) throws IOException {
        return Files.newBufferedWriter(path, CHARSET_UTF8, CREATE, WRITE, TRUNCATE_EXISTING);
    }
}

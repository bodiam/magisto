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

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import static nl.ulso.magisto.io.DummyPathEntry.createPathEntry;
import static nl.ulso.magisto.io.Paths.createPath;

public class DummyFileSystem implements FileSystem {

    private final Path sourceRoot;
    private final Path staticRoot;
    private final Path targetRoot;
    private final Set<DummyPathEntry> sourcePaths = new HashSet<>();
    private final Set<DummyPathEntry> staticPaths = new HashSet<>();
    private final Set<DummyPathEntry> targetPaths = new HashSet<>();
    private final Map<String, String> textFilesForReading = new HashMap<>();
    private final Map<String, StringWriter> textFilesForWriting = new HashMap<>();

    private String loggedCopies = "";
    private String loggedDeletions = "";

    public DummyFileSystem() {
        this.sourceRoot = createPath("source").toAbsolutePath();
        this.staticRoot = this.sourceRoot.resolve(".static");
        this.targetRoot = createPath("target").toAbsolutePath();
    }

    public Path getSourceRoot() {
        return sourceRoot;
    }

    public Path getStaticRoot() {
        return staticRoot;
    }

    public Path getTargetRoot() {
        return targetRoot;
    }

    @Override
    public boolean isDirectory(Path path) {
        return true;
    }

    @Override
    public boolean isReadable(Path path) {
        return true;
    }

    @Override
    public boolean isWritable(Path path) {
        return true;
    }

    @Override
    public boolean isEmpty(Path directory) throws IOException {
        return true;
    }

    @Override
    public void createDirectories(Path path) throws IOException {
    }

    @Override
    public void createFile(Path file) throws IOException {
    }

    @Override
    public Path toRealPath(Path path) throws IOException {
        return path;
    }

    @Override
    public void delete(Path path) throws IOException {
        loggedDeletions += String.format("%s (%s)%n", path.getFileName(), path);
    }

    @Override
    public SortedSet<Path> findAllPaths(Path root) throws IOException {
        return findAllPaths(root, DEFAULT_PATH_FILTER, DEFAULT_PATH_COMPARATOR);
    }

    @Override
    public SortedSet<Path> findAllPaths(Path root, Comparator<? super Path> comparator) throws IOException {
        return findAllPaths(root, DEFAULT_PATH_FILTER, comparator);
    }

    @Override
    public SortedSet<Path> findAllPaths(Path root, PathFilter filter) throws IOException {
        return findAllPaths(root, filter, DEFAULT_PATH_COMPARATOR);
    }

    @Override
    public SortedSet<Path> findAllPaths(Path root, PathFilter filter, Comparator<? super Path> comparator) throws IOException {
        final SortedSet<Path> paths = new TreeSet<>(comparator);
        if (root.equals(sourceRoot)) {
            addAllPaths(paths, sourcePaths, filter);
        } else if (root.equals(staticRoot)) {
            addAllPaths(paths, staticPaths, filter);
        } else if (root.equals(targetRoot)) {
            addAllPaths(paths, targetPaths, filter);
        }
        return paths;
    }

    @Override
    public long getLastModifiedInMillis(Path path) throws IOException {
        final DummyPathEntry entry;
        if (path.startsWith(staticRoot)) {
            entry = findEntry(staticRoot.relativize(path), staticPaths);
        } else if (path.startsWith(sourceRoot)) {
            entry = findEntry(sourceRoot.relativize(path), sourcePaths);
        } else if (path.startsWith(targetRoot)) {
            entry = findEntry(targetRoot.relativize(path), targetPaths);
        } else {
            entry = null;
        }
        return entry != null ? entry.getTimestamp() : -1;
    }

    private void addAllPaths(SortedSet<Path> paths, Set<DummyPathEntry> entries, PathFilter filter) {
        for (DummyPathEntry entry : entries) {
            final Path path = entry.getPath();
            if (filter.acceptFile(path)) {
                paths.add(path);
            }
        }
    }

    @Override
    public void copy(Path sourceRoot, Path targetRoot, Path path) throws IOException {
        loggedCopies += String.format("%s:%s -> %s%n", sourceRoot.getFileName(), path, targetRoot.getFileName());
    }

    @Override
    public BufferedReader newBufferedReaderForTextFile(Path path) throws IOException {
        final String contents = textFilesForReading.get(path.getFileName().toString());
        if (contents == null) {
            throw new FileNotFoundException(path.getFileName().toString());
        }
        return new BufferedReader(new StringReader(contents));
    }

    @Override
    public BufferedWriter newBufferedWriterForTextFile(Path path) throws IOException {
        final StringWriter writer = new StringWriter();
        textFilesForWriting.put(path.getFileName().toString(), writer);
        return new BufferedWriter(writer);
    }

    @Override
    public boolean exists(Path path) {
        if (path.equals(sourceRoot) || path.equals(targetRoot)) {
            return true;
        }
        if (path.equals(staticRoot)) {
            return !staticPaths.isEmpty();
        }
        if (path.startsWith(targetRoot)) {
            return targetPaths.contains(createPathEntry(path));
        }
        return staticPaths.contains(createPathEntry(path))
                || textFilesForReading.containsKey(path.getFileName().toString());
    }

    @Override
    public boolean notExists(Path path) {
        return !exists(path);
    }

    private DummyPathEntry findEntry(Path source, Set<DummyPathEntry> entries) {
        for (DummyPathEntry entry : entries) {
            if (entry.getPath().equals(source)) {
                return entry;
            }
        }
        throw new IllegalStateException("Should not get here!");
    }

    public void addSourcePaths(DummyPathEntry... paths) {
        sourcePaths.addAll(Arrays.asList(paths));
    }

    public void addStaticPaths(DummyPathEntry... paths) {
        staticPaths.addAll(Arrays.asList(paths));
    }

    public void addTargetPaths(DummyPathEntry... paths) {
        targetPaths.addAll(Arrays.asList(paths));
    }

    public String getLoggedCopies() {
        return loggedCopies.trim();
    }

    public String getLoggedDeletions() {
        return loggedDeletions.trim();
    }

    public void registerTextFileForBufferedReader(String fileName, String content) {
        textFilesForReading.put(fileName, content);
    }

    public String getTextFileFromBufferedWriter(String fileName) {
        return textFilesForWriting.get(fileName).toString();
    }
}

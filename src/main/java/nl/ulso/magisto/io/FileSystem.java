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
import java.nio.file.Path;
import java.util.Comparator;
import java.util.SortedSet;

/**
 * Handles all file system access done by Magisto.
 * <p>
 * Yet another filesystem abstraction? Really?
 * </p>
 * <p>
 * This application is doing a lot of file system access: reading files, loading template files, copying files, reading
 * and writing directories... All file system access is here, nicely isolated, so that it can easily be swapped out
 * in unit tests.
 * </p>
 */
public interface FileSystem {

    /**
     * Name of the empty file to write after every export.
     */
    static final String MAGISTO_EXPORT_MARKER_FILE = ".magisto-export";

    static final Comparator<? super Path> DEFAULT_PATH_COMPARATOR = new DefaultPathComparator();

    static final PathFilter DEFAULT_PATH_FILTER = new DefaultPathFilter();

    /**
     * Resolves and checks the source directory.
     * <p>
     * If the directory is not according to the rules, this method throws an {@link IOException}. The rules are:
     * </p>
     * <ul>
     * <li>It must exist</li>
     * <li>It must be a directory</li>
     * <li>It must be readable</li>
     * </li>
     * </ul>
     * <p>
     * The path returned is an absolute path.
     * </p>
     *
     * @param directoryName Name of the source directory.
     * @return Existing, valid, real path to the directory.
     * @throws IOException if the path couldn't be resolved or if it isn't valid.
     */
    Path resolveSourceDirectory(String directoryName) throws IOException;

    /**
     * Prepares the target directory.
     * <p>
     * If the target directory doesn't yet exist, it will be created.
     * </p>
     * <p>
     * If the target does exist, it must be according to these rules:
     * </p>
     * <ul>
     * <li>It must be a directory</li>
     * <li>It must be writable</li>
     * <li>It must be empty, or it must have a file called "{@value #MAGISTO_EXPORT_MARKER_FILE}".</li>
     * </ul>
     * <p>
     * The path returned is an absolute path.
     * </p>
     *
     * @param directoryName Name of the target directory.
     * @return Existing, valid, real path to the directory.
     * @throws IOException If an exception occurs while accessing the file system.
     */
    Path prepareTargetDirectory(String directoryName) throws IOException;

    /**
     * Ensures that the source and target directories do not overlap
     *
     * @param sourceRoot Source directory, must be a real path
     * @param targetRoot Target directory, must be a real path
     * @throws IOException If the directories overlap.
     */
    void requireDistinct(Path sourceRoot, Path targetRoot) throws IOException;

    /**
     * Writes the {@value #MAGISTO_EXPORT_MARKER_FILE} to the directory.
     *
     * @param targetRoot Directory, must be a real path.
     */
    void writeTouchFile(Path targetRoot) throws IOException;

    /**
     * @param targetRoot Target directory, must be a real path;
     * @return The last modified timestamp of the touch file, or {@value -1} if the file doesn't exist.
     * @throws IOException If an exception occurred with accessing the touch file.
     */
    long getTouchFileLastModifiedInMillis(Path targetRoot) throws IOException;

    /**
     * @param root Directory to find all paths in.
     * @return All paths in a directory, all relative to the directory itself.
     * @throws IOException If an exception occurs while finding all paths.
     */
    SortedSet<Path> findAllPaths(Path root) throws IOException;

    /**
     * @param root       Directory to find all paths in.
     * @param comparator Comparator to use for path comparisons.
     * @return All paths in a directory, all relative to the directory itself.
     * @throws IOException If an exception occurs while finding all paths.
     */
    SortedSet<Path> findAllPaths(Path root, Comparator<? super Path> comparator) throws IOException;

    /**
     * @param root   Directory to find all paths in.
     * @param filter Filter to apply to the paths found.
     * @return All paths in a directory, all relative to the directory itself.
     * @throws IOException If an exception occurs while finding all paths.
     */
    SortedSet<Path> findAllPaths(Path root, PathFilter filter) throws IOException;

    /**
     * @param root       Directory to find all paths in.
     * @param filter     Filter to apply to the paths found.
     * @param comparator Comparator to use for path comparisons.
     * @return All paths in a directory, all relative to the directory itself.
     * @throws IOException If an exception occurs while finding all paths.
     */
    SortedSet<Path> findAllPaths(Path root, PathFilter filter, Comparator<? super Path> comparator) throws IOException;

    /**
     * @param path Absolute path to get the last modified timestamp of.
     * @return Last modified timestamp of {@code path}
     */
    long getLastModifiedInMillis(Path path) throws IOException;

    /**
     * Copies {@code path} in {@code sourceRoot} to {@code targetRoot}, overwriting the same path in the target
     * directory if it already exists.
     *
     * @param sourceRoot Absolute path to the source directory.
     * @param targetRoot Absolute path to the target directory.
     * @param path       Relative path to the file or directory to copy within the source directory.
     */
    void copy(Path sourceRoot, Path targetRoot, Path path) throws IOException;

    /**
     * Deletes a path from the root.
     *
     * @param root Absolute path to the directory.
     * @param path Relative path to the file or directory to deleteTarget within the root directory.
     */
    void delete(Path root, Path path) throws IOException;

    /**
     * @return A new reader for a text file in UTF-8.
     * @throws IOException If an exception accessing occurs while accessing the file system.
     */
    BufferedReader newBufferedReaderForTextFile(Path path) throws IOException;

    /**
     * @return A new writer for a text file in UTF-8; if a file already exists it is overwritten.
     * @throws IOException If an exception accessing occurs while accessing the file system.
     */
    BufferedWriter newBufferedWriterForTextFile(Path path) throws IOException;

    /**
     * @param path Absolute path to check for existence.
     * @return {@code true} if the file exists, {@code false} if it doesn't.
     */
    boolean exists(Path path);

    /**
     * @param path Absolute path to check for existence.
     * @return {@code true} if the file does not exist, {@code false} if it does.
     */
    boolean notExists(Path path);
}

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

    static final Comparator<? super Path> DEFAULT_PATH_COMPARATOR = new DefaultPathComparator();

    static final PathFilter DEFAULT_PATH_FILTER = new DefaultPathFilter();

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
     * @param path Absolute path to check for existence.
     * @return {@code true} if the file exists, {@code false} if it doesn't.
     */
    boolean exists(Path path);

    /**
     * @param path Absolute path to check for existence.
     * @return {@code true} if the file does not exist, {@code false} if it does.
     */
    boolean notExists(Path path);

    /**
     * @param path Absolute path to check if it is a directory.
     * @return {@code true} if the path points to a directory, {@code false} otherwise.
     */
    boolean isDirectory(Path path);

    /**
     * @param path Absolute path to check for readability.
     * @return {@code true} if the path is readable, {@code false} otherwise.
     */
    boolean isReadable(Path path);

    /**
     * @param path Absolute path to check for writeability.
     * @return {@code true} if the path is writable, {@code false} otherwise.
     */
    boolean isWritable(Path path);

    /**
     * @param directory Absolute path to a directory to check for emptiness.
     * @return {@code true} if the directory is empty; {@code false} otherwise.
     * @throws IOException If the directory couldn't be read.
     */
    boolean isEmpty(Path directory) throws IOException;

    /**
     * @param path Abssolute path to convert to a real path. Use this method instead {@link
     *             java.nio.file.Path#toRealPath} for better testability.
     * @return The real path of the path.
     * @throws IOException If the conversion couldn't be performed.
     */
    Path toRealPath(Path path) throws IOException;

    /**
     * @param file Absolute path to the file to create.
     * @throws IOException If the file couldn't be created.
     */
    void createFile(Path file) throws IOException;

    /**
     * @param path Absolute path representing the directory to create, including any missing superdirectories.
     * @throws IOException If any of the required directories couldn't be created.
     */
    void createDirectories(Path path) throws IOException;

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
     * @param path Absolute path to the file or directory to delete.
     */
    void delete(Path path) throws IOException;

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
}

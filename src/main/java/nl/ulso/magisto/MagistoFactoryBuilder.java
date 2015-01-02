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

import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;

/**
 * Builder for the {@link nl.ulso.magisto.MagistoFactory}.
 * <p>
 * This is what you need to do if you don't want to use heavy DI frameworks and you want to keep strict track of your
 * dependencies yourself...
 * </p>
 * <p>
 * By having a builder that in turn creates a factory, the system now ensures that all components that are created work
 * against the same source and target directories. There's no easy way to mess up; the code doesn't allow it.
 * </p>
 */
public interface MagistoFactoryBuilder {

    FileSystem getFileSystem();

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
     * @param sourceDirectory Name of the source directory.
     * @return Existing, valid, real path to the directory.
     * @throws IOException if the path couldn't be resolved or if it isn't valid.
     */
    MagistoFactoryBuilder withSourceDirectory(String sourceDirectory) throws IOException;

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
     * <li>It must be empty, or it must have a file called "{@value TouchFile#TOUCH_FILE}".</li>
     * </ul>
     * <p>
     * The path returned is an absolute path.
     * </p>
     *
     * @param targetDirectory Name of the target directory.
     * @return Existing, valid, real path to the directory.
     * @throws IOException If an exception occurs while accessing the file system.
     */
    MagistoFactoryBuilder withTargetDirectory(String targetDirectory) throws IOException;

    /**
     * @return A factory for components that run with the provides source and target roots.
     * @throws java.lang.IllegalStateException If the source and target roots overlap.
     */
    MagistoFactory build();
}

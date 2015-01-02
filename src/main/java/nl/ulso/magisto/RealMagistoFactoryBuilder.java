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

import nl.ulso.magisto.git.GitClient;
import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;
import static nl.ulso.magisto.io.Paths.createPath;

class RealMagistoFactoryBuilder implements MagistoFactoryBuilder {

    private final FileSystem fileSystem;
    private final GitClient gitClient;
    private Path sourceRoot;
    private Path targetRoot;

    RealMagistoFactoryBuilder(FileSystem fileSystem, GitClient gitClient) {
        this.fileSystem = fileSystem;
        this.gitClient = gitClient;
    }

    @Override
    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public MagistoFactoryBuilder withSourceDirectory(String sourceDirectory) throws IOException {
        final Path path = createPath(sourceDirectory);
        if (fileSystem.notExists(path)) {
            throw new NoSuchFileException(path.toString());
        }
        if (!fileSystem.isDirectory(path)) {
            throw new IOException("Not a directory: " + path);
        }
        if (!fileSystem.isReadable(path)) {
            throw new IOException("Directory not readable: " + path);
        }
        this.sourceRoot = fileSystem.toRealPath(path);
        return this;
    }

    @Override
    public MagistoFactoryBuilder withTargetDirectory(String targetDirectory) throws IOException {
        final Path path = createPath(targetDirectory);
        if (fileSystem.notExists(path)) {
            fileSystem.createDirectories(path);
        }
        if (!fileSystem.isDirectory(path)) {
            throw new IOException("Not a directory: " + path);
        }
        if (!fileSystem.isReadable(path)) {
            throw new IOException("Directory not readable: " + path);
        }
        if (!fileSystem.isWritable(path)) {
            throw new IOException("Directory not writable: " + path);
        }
        if (!fileSystem.isEmpty(path)) {
            final Path touchFile = path.resolve(TouchFile.TOUCH_FILE);
            if (fileSystem.notExists(touchFile)) {
                throw new IOException("Directory not empty and not an export: " + path);
            }
        }
        this.targetRoot = fileSystem.toRealPath(path);
        return this;
    }

    @Override
    public MagistoFactory build() {
        requireNonNull(sourceRoot);
        requireNonNull(targetRoot);
        if (targetRoot.startsWith(sourceRoot)) {
            throw new IllegalStateException("The target directory may not be inside the source directory");
        }
        if (sourceRoot.startsWith(targetRoot)) {
            throw new IllegalStateException("The source directory may not be inside the target directory");
        }
        return new RealMagistoFactory(fileSystem, gitClient, sourceRoot, targetRoot);
    }

    Path getSourceRoot() {
        return sourceRoot;
    }

    Path getTargetRoot() {
        return targetRoot;
    }
}

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

package nl.ulso.magisto;

import nl.ulso.magisto.git.GitClient;
import nl.ulso.magisto.io.FileSystem;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;
import static nl.ulso.magisto.io.Paths.requireAbsolutePath;

class RealMagistoFactoryBuilder implements MagistoFactoryBuilder {

    private final FileSystem filesystem;
    private final GitClient gitClient;
    private Path sourceRoot;
    private Path targetRoot;

    RealMagistoFactoryBuilder(FileSystem filesystem, GitClient gitClient) {
        this.filesystem = filesystem;
        this.gitClient = gitClient;
    }

    @Override
    public FileSystem getFileSystem() {
        return filesystem;
    }

    @Override
    public MagistoFactoryBuilder withSourceRoot(Path sourceRoot) {
        this.sourceRoot = requireAbsolutePath(sourceRoot);
        return this;
    }

    @Override
    public MagistoFactoryBuilder withTargetRoot(Path targetRoot) {
        this.targetRoot = requireAbsolutePath(targetRoot);
        return this;
    }

    @Override
    public MagistoFactory build() {
        requireNonNull(sourceRoot);
        requireNonNull(targetRoot);
        return new RealMagistoFactory(filesystem, gitClient, sourceRoot, targetRoot);
    }
}

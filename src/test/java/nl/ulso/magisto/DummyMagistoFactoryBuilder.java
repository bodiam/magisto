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

import nl.ulso.magisto.action.DummyActionFactory;
import nl.ulso.magisto.io.DummyFileSystem;
import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;

public class DummyMagistoFactoryBuilder implements MagistoFactoryBuilder {

    private final FileSystem fileSystem;
    private final Path sourceRoot;
    private final Path targetRoot;
    private boolean isCustomTemplateChanged = false;
    private DummyActionFactory actionFactory = new DummyActionFactory();

    public DummyMagistoFactoryBuilder() {
        fileSystem = new DummyFileSystem();
        sourceRoot = ((DummyFileSystem) fileSystem).getSourceRoot();
        targetRoot = ((DummyFileSystem) fileSystem).getTargetRoot();
    }

    @Override
    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public MagistoFactoryBuilder withSourceDirectory(String sourceDirectory) throws IOException {
        return this;
    }

    @Override
    public MagistoFactoryBuilder withTargetDirectory(String targetDirectory) throws IOException {
        return this;
    }

    public DummyMagistoFactoryBuilder setCustomTemplateChanged() {
        isCustomTemplateChanged = true;
        return this;
    }

    @Override
    public MagistoFactory build() {
        return new DummyMagistoFactory(fileSystem, sourceRoot, targetRoot, actionFactory, isCustomTemplateChanged);
    }

    public DummyActionFactory getDummyActionFactory() {
        return actionFactory;
    }
}

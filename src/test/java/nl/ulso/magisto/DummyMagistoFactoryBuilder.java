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

import java.nio.file.Path;

public class DummyMagistoFactoryBuilder implements MagistoFactoryBuilder {

    private final FileSystem fileSystem = new DummyFileSystem();
    private Path sourceRoot;
    private Path targetRoot;
    private boolean isCustomTemplateChanged = false;
    private DummyActionFactory actionFactory = new DummyActionFactory();

    @Override
    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public MagistoFactoryBuilder withSourceRoot(Path sourceRoot) {
        this.sourceRoot = sourceRoot;
        return this;
    }

    @Override
    public MagistoFactoryBuilder withTargetRoot(Path targetRoot) {
        this.targetRoot = targetRoot;
        return this;
    }

    public DummyMagistoFactoryBuilder setCustomTemplateChanged() {
        isCustomTemplateChanged = true;
        return this;
    }

    @Override
    public MagistoFactory build() {
        return new DummyMagistoFactory(sourceRoot, targetRoot, actionFactory, isCustomTemplateChanged);
    }

    public DummyActionFactory getDummyActionFactory() {
        return actionFactory;
    }
}

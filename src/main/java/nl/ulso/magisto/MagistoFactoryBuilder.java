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

import nl.ulso.magisto.io.FileSystem;

import java.nio.file.Path;

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

    MagistoFactoryBuilder withSourceRoot(Path sourceRoot);

    MagistoFactoryBuilder withTargetRoot(Path targetRoot);

    MagistoFactory build();
}

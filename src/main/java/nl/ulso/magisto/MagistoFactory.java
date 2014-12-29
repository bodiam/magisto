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

import nl.ulso.magisto.action.ActionFactory;
import nl.ulso.magisto.converter.DocumentConverter;
import nl.ulso.magisto.loader.DocumentLoader;

import java.nio.file.Path;

/**
 * Factory for the main components of Magisto.
 */
public interface MagistoFactory {

    /**
     * @param sourceRoot Absolute path that serves as the root for all documents to load.
     * @return A document loader for documents in {@code sourceRoot}.
     */
    DocumentLoader createDocumentLoader(Path sourceRoot);

    /**
     * @param sourceRoot Absolute path that serves as the root for all documents to load.
     * @param targetRoot Absolute path that serves as the root for all conversion results.
     * @return A document converter that reads documents in {@code sourceRoot} and stores them in {@code targetRoot}.
     */
    DocumentConverter createDocumentConverter(Path sourceRoot, Path targetRoot);

    /**
     * @param sourceRoot Absolute path that serves as the source root for actions.
     * @param targetRoot Absolute path that serves as the target root for actions.
     * @return A factory for actions that perform on {@code sourceRoot} and/or {@code targetRoot}.
     */
    ActionFactory createActionFactory(Path sourceRoot, Path targetRoot);
}

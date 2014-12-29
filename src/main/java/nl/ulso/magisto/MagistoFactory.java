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
     * @return The path that serves as the root for all sources accessed by components created by this factory.
     */
    Path getSourceRoot();

    /**
     * @return The path that serves as the root for all targets accessed by components created by this factory.
     */
    Path getTargetRoot();

    /**
     * @return A loader for documents in the source root.
     */
    DocumentLoader createDocumentLoader();

    /**
     * @return A converter for documents in the source root that stores the results in the target root.
     */
    DocumentConverter createDocumentConverter();

    /**
     * @return A factory for actions that operate on files in the the source and/or target roots.
     */
    ActionFactory createActionFactory();
}

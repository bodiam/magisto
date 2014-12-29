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

public interface MagistoFactory {

    DocumentLoader createDocumentLoader(Path sourceRoot);

    DocumentConverter createDocumentConverter(Path sourceRoot, Path targetRoot);

    ActionFactory createActionFactory(Path sourceRoot, Path targetRoot);
}

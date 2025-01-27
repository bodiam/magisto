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

package nl.ulso.magisto.action;

import nl.ulso.magisto.converter.FileConverter;

import java.nio.file.Path;

/**
 * Factory for all the various types of actions.
 */
public interface ActionFactory {

    Action skipSource(Path path);

    Action skipStatic(Path path);

    Action copySource(Path path);

    Action copyStatic(Path path, String staticContentDirectory);

    Action convertSource(Path path, FileConverter fileConverter);

    Action deleteTarget(Path path);
}

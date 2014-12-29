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

import java.nio.file.Path;

/**
 * Factory for all the various types of actions.
 */
public interface ActionFactory {

    /**
     * @param path Path in the source tree to skip because it hasn't changed since the last export.
     * @return Skip action, useful for statistics tracking.
     */
    Action skipSource(Path path);

    /**
     * @param path Path in the static tree to skip because it hasn't changed since the last export.
     * @return Skip action, useful for statistics tracking.
     */
    Action skipStatic(Path path);

    /**
     * @param path Path to copy from the source tree to the target tree.
     * @return Copy action.
     */
    Action copySource(Path path);

    /**
     * @param path Path to copy from the static tree to the target tree.
     * @return Copy action.
     */
    Action copyStatic(Path path);

    /**
     * @param path Path to a document in the source tree to convert to a different format in the target tree.
     * @return Convert action.
     */
    Action convertSource(Path path);

    /**
     * @param path Path to a document in the target tree that must be deleted because it's no longer present in the
     *             source or static tree.
     * @return Delete action.
     */
    Action deleteTarget(Path path);
}

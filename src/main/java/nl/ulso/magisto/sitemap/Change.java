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

package nl.ulso.magisto.sitemap;

import java.nio.file.Path;

import static nl.ulso.magisto.io.Paths.requireRelativePath;

/**
 * Represents a change to a single page
 */
public class Change {

    private final ChangeType type;
    private final Path path;

    public Change(ChangeType type, Path path) {
        requireRelativePath(path);
        this.type = type;
        this.path = path;
    }

    public ChangeType getChangeType() {
        return type;
    }

    public Path getPath() {
        return path;
    }
}
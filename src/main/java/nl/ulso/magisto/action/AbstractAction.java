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

import static nl.ulso.magisto.io.Paths.requireRelativePath;

/**
 * Abstract base class for {@link Action}s.
 */
abstract class AbstractAction implements Action {
    private final Path path;
    private final ActionCategory category;

    AbstractAction(Path path, ActionCategory category) {
        this.path = requireRelativePath(path);
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AbstractAction that = (AbstractAction) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    public Path getPath() {
        return path;
    }

    @Override
    public ActionCategory getActionCategory() {
        return category;
    }
}

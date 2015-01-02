/*
 * Copyright 2015 Vincent Oostindie
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

package nl.ulso.magisto.io;

import java.nio.file.Path;

/**
 * Represents a path that is split on extension.
 * <p>
 * Two split paths are the same if the paths <strong>with their extensions removed</strong> are the same.
 * </p>
 */
public class SplitPath {

    private final Path pathWithoutExtension;
    private final String originalExtension;

    SplitPath(Path path) {
        final String filename = path.getName(path.getNameCount() - 1).toString();
        final int position = filename.lastIndexOf('.');
        if (position < 1) {
            pathWithoutExtension = path;
            originalExtension = "";
        } else {
            pathWithoutExtension = path.resolveSibling(filename.substring(0, position));
            originalExtension = filename.substring(position + 1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SplitPath that = (SplitPath) o;
        return pathWithoutExtension.equals(that.pathWithoutExtension);
    }

    @Override
    public int hashCode() {
        return pathWithoutExtension.hashCode();
    }

    public Path getPathWithoutExtension() {
        return pathWithoutExtension;
    }

    public String getOriginalExtension() {
        return originalExtension;
    }
}

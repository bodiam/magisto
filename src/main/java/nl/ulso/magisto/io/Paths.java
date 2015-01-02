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

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Set;

/**
 * Utility methods on {@link Path}s.
 */
public final class Paths {

    private Paths() {
    }

    public static Path requireAbsolutePath(Path path) {
        if (!path.isAbsolute()) {
            throw new IllegalStateException("Not an absolute path: " + path);
        }
        return path;
    }

    public static Path requireRelativePath(Path path) {
        if (path.isAbsolute()) {
            throw new IllegalStateException("Not a relative path: " + path);
        }
        return path;
    }

    public static Path createPath(String first, String... more) {
        return FileSystems.getDefault().getPath(first, more);
    }

    public static SplitPath splitOnExtension(Path path) {
        return new SplitPath(path);
    }

    public static Comparator<Path> prioritizeOnExtension(String... extensions) {
        return new PrioritizedByExtensionPathComparator(extensions);
    }

    public static Comparator<Path> prioritizeOnExtension(Set<String> extensions) {
        return new PrioritizedByExtensionPathComparator(extensions);
    }

}

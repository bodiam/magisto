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

package nl.ulso.magisto.io;

import java.nio.file.Path;
import java.util.*;

import static nl.ulso.magisto.io.Paths.splitOnExtension;

/**
 * Comparator for paths. Implements a normal path comparison, unless the path differ only on extension. In that case
 * the paths are ordered on extension priority first.
 * <p>
 * Extensions are always translated to lower case before comparison.
 * </p>
 *
 * @see nl.ulso.magisto.io.Paths#prioritizeOnExtension(String...)
 * @see nl.ulso.magisto.io.Paths#prioritizeOnExtension(java.util.Set)
 */
class PrioritizedByExtensionPathComparator implements Comparator<Path> {
    private final Set<String> extensions;

    PrioritizedByExtensionPathComparator(String[] extensions) {
        this(Arrays.asList(extensions));
    }

    PrioritizedByExtensionPathComparator(Collection<String> extensions) {
        final HashSet<String> set = new HashSet<>(extensions.size());
        for (String extension : extensions) {
            set.add(extension.toLowerCase());
        }
        this.extensions = Collections.unmodifiableSet(set);
    }

    @Override
    public int compare(Path first, Path second) {
        final SplitPath firstPath = splitOnExtension(first);
        final SplitPath secondPath = splitOnExtension(second);

        final int pathComparison = firstPath.getPathWithoutExtension().compareTo(secondPath.getPathWithoutExtension());
        if (pathComparison != 0) {
            return pathComparison;
        }

        final String firstExtension = firstPath.getOriginalExtension();
        if (extensions.contains(firstExtension.toLowerCase())) {
            return -1;
        }

        final String secondExtension = secondPath.getOriginalExtension();
        if (extensions.contains(secondExtension.toLowerCase())) {
            return 1;
        }

        return firstExtension.compareTo(secondExtension);
    }
}

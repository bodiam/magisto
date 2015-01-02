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
 * Filter for paths, used by {@link FileSystem#findAllPaths(java.nio.file.Path, PathFilter, java.util.Comparator)}.
 * <p>
 * Paths passed to the filter are always <strong>relative</strong> paths within a root.
 * </p>
 */
public interface PathFilter {

    boolean acceptDirectory(Path path);

    boolean acceptFile(Path path);
}

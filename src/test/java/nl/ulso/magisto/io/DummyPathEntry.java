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

import static nl.ulso.magisto.io.Paths.createPath;

public class DummyPathEntry {

    private final Path path;
    private final long timestamp;

    private DummyPathEntry(Path path, long timestamp) {
        this.path = path;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DummyPathEntry that = (DummyPathEntry) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    public static DummyPathEntry createPathEntry(String fileName) {
        return new DummyPathEntry(createPath(fileName), System.currentTimeMillis());
    }

    public static DummyPathEntry createPathEntry(Path path) {
        return new DummyPathEntry(createPath(path.getFileName().toString()), System.currentTimeMillis());
    }

    public static DummyPathEntry createPathEntry(String first, String... more) {
        return new DummyPathEntry(createPath(first, more), System.currentTimeMillis());
    }

    public Path getPath() {
        return path;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

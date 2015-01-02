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

package nl.ulso.magisto.converter.freemarker;

import freemarker.cache.TemplateLoader;
import nl.ulso.magisto.io.FileSystem;
import nl.ulso.magisto.io.Paths;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

/**
 * FreeMarker {@link TemplateLoader} that loads templates from the file system using the
 * {@link nl.ulso.magisto.io.FileSystem}.
 */
class CustomTemplateLoader implements TemplateLoader {

    private final FileSystem fileSystem;
    private final Path templateRoot;

    public CustomTemplateLoader(FileSystem fileSystem, Path templateRoot) {
        this.fileSystem = fileSystem;
        this.templateRoot = templateRoot;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        final String[] parts = name.split("/");
        if (parts.length != 1) {
            throw new UnsupportedOperationException("Template sources in directories are not supported: " + name);
        }
        final Path path = templateRoot.resolve(Paths.createPath(parts[0]));
        if (fileSystem.exists(path)) {
            return path;
        }
        return null;
    }

    @Override
    public long getLastModified(Object templateSource) {
        final Path path = (Path) templateSource;
        try {
            return fileSystem.getLastModifiedInMillis(path);
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        final Path path = (Path) templateSource;
        return fileSystem.newBufferedReaderForTextFile(path);
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        // Nothing to do here.
    }
}

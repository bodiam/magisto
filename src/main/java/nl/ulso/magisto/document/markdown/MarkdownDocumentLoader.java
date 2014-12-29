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

package nl.ulso.magisto.document.markdown;

import nl.ulso.magisto.document.Document;
import nl.ulso.magisto.document.DocumentLoader;
import nl.ulso.magisto.io.FileSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static nl.ulso.magisto.io.Paths.splitOnExtension;

public class MarkdownDocumentLoader implements DocumentLoader {

    static final Set<String> MARKDOWN_EXTENSIONS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("md", "markdown", "mdown")));

    private final FileSystem fileSystem;

    public MarkdownDocumentLoader(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public Set<String> getSupportedExtensions() {
        return MARKDOWN_EXTENSIONS;
    }

    @Override
    public boolean supports(Path path) {
        return MARKDOWN_EXTENSIONS.contains(splitOnExtension(path).getOriginalExtension().toLowerCase());
    }

    @Override
    public Document loadDocument(Path path) throws IOException {
        try (final BufferedReader reader = fileSystem.newBufferedReaderForTextFile(path)) {
            final StringBuilder builder = new StringBuilder();
            while (reader.ready()) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                builder.append(line);
                builder.append(System.lineSeparator());
            }
            return new MarkdownDocument(builder.toString().toCharArray());
        }
    }
}

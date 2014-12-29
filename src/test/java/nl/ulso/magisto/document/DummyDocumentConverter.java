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

package nl.ulso.magisto.document;

import java.io.IOException;
import java.nio.file.Path;

public class DummyDocumentConverter implements DocumentConverter {

    private final boolean isCustomTemplateChanged;
    private String loggedConversions = "";

    public DummyDocumentConverter() {
        this(false);
    }

    public DummyDocumentConverter(boolean isCustomTemplateChanged) {
        this.isCustomTemplateChanged = isCustomTemplateChanged;
    }

    @Override
    public String getTargetExtension() {
        return "converted";
    }

    @Override
    public Path getConvertedFileName(Path path) {
        return path.resolveSibling(path.getFileName().toString() + "ed");
    }

    @Override
    public void convert(Path sourceRoot, Path targetRoot, Path path)
            throws IOException {
        loggedConversions += String.format("%s:%s -> %s:%s", sourceRoot.getFileName(), path.getFileName(),
                targetRoot.getFileName(), getConvertedFileName(path).getFileName());
    }

    @Override
    public boolean isCustomTemplateChanged(Path sourceRoot, Path targetRoot)
            throws IOException {
        return isCustomTemplateChanged;
    }

    public String getLoggedConversions() {
        return loggedConversions;
    }

    public void clearRecordings() {
        loggedConversions = "";
    }
}

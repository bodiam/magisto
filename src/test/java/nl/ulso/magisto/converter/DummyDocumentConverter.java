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

package nl.ulso.magisto.converter;

import java.io.IOException;
import java.nio.file.Path;

import static nl.ulso.magisto.io.Paths.createPath;

public class DummyDocumentConverter implements DocumentConverter {

    private final Path sourceRoot;
    private final Path targetRoot;
    private final boolean isCustomTemplateChanged;
    private String loggedConversions = "";

    public DummyDocumentConverter() {
        this(createPath("source"), createPath("target"), false);
    }

    public DummyDocumentConverter(Path sourceRoot, Path targetRoot) {
        this(sourceRoot, targetRoot, false);
    }

    public DummyDocumentConverter(Path sourceRoot, Path targetRoot, boolean isCustomTemplateChanged) {
        this.sourceRoot = sourceRoot;
        this.targetRoot = targetRoot;
        this.isCustomTemplateChanged = isCustomTemplateChanged;
    }

    @Override
    public String getTargetExtension() {
        return "converted";
    }

    @Override
    public Path getSourceRoot() {
        return sourceRoot;
    }

    @Override
    public Path getTargetRoot() {
        return targetRoot;
    }

    @Override
    public Path getConvertedFileName(Path path) {
        return path.resolveSibling(path.getFileName().toString() + "ed");
    }

    @Override
    public void convert(Path path) throws IOException {
        loggedConversions += String.format("%s:%s -> %s:%s", sourceRoot.getFileName(), path.getFileName(),
                targetRoot.getFileName(), getConvertedFileName(path).getFileName());
    }

    @Override
    public boolean isCustomTemplateChanged() throws IOException {
        return isCustomTemplateChanged;
    }

    public String getLoggedConversions() {
        return loggedConversions;
    }
}

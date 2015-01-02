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

package nl.ulso.magisto;

import nl.ulso.magisto.converter.DocumentConverter;
import nl.ulso.magisto.io.PathFilter;
import nl.ulso.magisto.io.Paths;
import nl.ulso.magisto.io.SplitPath;
import nl.ulso.magisto.loader.DocumentLoader;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Path filter that skips all automatically generated index files. These are all index files that do not have a
 * corresponding custom index file in the source paths.
 */
class SkipGeneratedIndexFilesFilter implements PathFilter {

    private final Set<SplitPath> customIndexes;
    private final String targetExtension;

    public SkipGeneratedIndexFilesFilter(Set<Path> sourcePaths, DocumentLoader documentLoader,
                                         DocumentConverter documentConverter) {
        this.customIndexes = findCustomIndexFiles(sourcePaths, createExtensionSet(documentLoader, documentConverter));
        this.targetExtension = documentConverter.getTargetExtension();
    }

    @Override
    public boolean acceptDirectory(Path path) {
        return true;
    }

    @Override
    public boolean acceptFile(Path path) {
        final SplitPath splitPath = Paths.splitOnExtension(path);
        final String fileName = splitPath.getPathWithoutExtension().getFileName().toString();
        final String extension = splitPath.getOriginalExtension();

        if (fileName.equalsIgnoreCase(Magisto.INDEX_FILE_NAME) && extension.equalsIgnoreCase(targetExtension)) {
            if (!customIndexes.contains(splitPath)) {
                return false;
            }
        }

        return true;
    }

    private Set<String> createExtensionSet(DocumentLoader documentLoader, DocumentConverter documentConverter) {
        final Set<String> supportedExtensions = documentLoader.getSupportedExtensions();
        final Set<String> extensions = new HashSet<>(supportedExtensions.size() + 1);
        extensions.addAll(supportedExtensions);
        extensions.add(documentConverter.getTargetExtension());
        return extensions;
    }

    private Set<SplitPath> findCustomIndexFiles(Set<Path> sourcePaths, Set<String> extensions) {

        final Set<SplitPath> customIndexes = new HashSet<>();

        for (Path path : sourcePaths) {
            final SplitPath splitPath = Paths.splitOnExtension(path);
            final String fileName = splitPath.getPathWithoutExtension().getFileName().toString();
            final String extension = splitPath.getOriginalExtension().toLowerCase();

            if (fileName.equalsIgnoreCase(Magisto.INDEX_FILE_NAME) && extensions.contains(extension)) {
                customIndexes.add(splitPath);
            }
        }

        return customIndexes;
    }
}

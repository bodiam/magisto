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

package nl.ulso.magisto;

import nl.ulso.magisto.action.ActionFactory;
import nl.ulso.magisto.action.RealActionFactory;
import nl.ulso.magisto.converter.DocumentConverter;
import nl.ulso.magisto.converter.freemarker.FreeMarkerDocumentConverter;
import nl.ulso.magisto.git.GitClient;
import nl.ulso.magisto.io.FileSystem;
import nl.ulso.magisto.loader.DocumentLoader;
import nl.ulso.magisto.loader.markdown.MarkdownDocumentLoader;

import java.nio.file.Path;

/**
 * Factory implementation that caches its creations. It's purely single threaded!
 */
public class RealMagistoFactory implements MagistoFactory {

    private final FileSystem fileSystem;
    private final Path sourceRoot;
    private final Path targetRoot;
    private final DocumentLoader documentLoader;
    private final DocumentConverter documentConverter;
    private final ActionFactory actionFactory;

    public RealMagistoFactory(FileSystem filesystem, GitClient gitClient, Path sourceRoot, Path targetRoot) {
        this.fileSystem = filesystem;
        this.sourceRoot = sourceRoot;
        this.targetRoot = targetRoot;
        documentLoader = new MarkdownDocumentLoader(filesystem, sourceRoot, gitClient);
        documentConverter = new FreeMarkerDocumentConverter(filesystem, documentLoader, targetRoot);
        actionFactory = new RealActionFactory(filesystem, documentConverter);
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
    public DocumentLoader createDocumentLoader() {
        return documentLoader;
    }

    @Override
    public DocumentConverter createDocumentConverter() {
        return documentConverter;
    }

    @Override
    public ActionFactory createActionFactory() {
        return actionFactory;
    }
}
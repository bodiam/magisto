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
import nl.ulso.magisto.loader.DocumentLoader;
import nl.ulso.magisto.converter.freemarker.FreeMarkerDocumentConverter;
import nl.ulso.magisto.loader.markdown.MarkdownDocumentLoader;
import nl.ulso.magisto.git.GitClient;
import nl.ulso.magisto.io.FileSystem;

import java.nio.file.Path;

import static nl.ulso.magisto.io.Paths.requireAbsolutePath;

/**
 * Factory implementation that caches its creations. It's purely single threaded!
 */
public class RealMagistoFactory implements MagistoFactory {

    private final FileSystem filesystem;
    private final GitClient gitClient;

    private DocumentLoader documentLoader;
    private DocumentConverter documentConverter;
    private ActionFactory actionFactory;

    public RealMagistoFactory(FileSystem filesystem, GitClient gitClient) {
        this.filesystem = filesystem;
        this.gitClient = gitClient;
        documentLoader = null;
        documentConverter = null;
        actionFactory = null;
    }

    @Override
    public DocumentLoader createDocumentLoader(Path sourceRoot) {
        if (documentLoader == null) {
            requireAbsolutePath(sourceRoot);
            documentLoader = new MarkdownDocumentLoader(filesystem, sourceRoot, gitClient);
        }
        return documentLoader;
    }

    @Override
    public DocumentConverter createDocumentConverter(Path sourceRoot, Path targetRoot) {
        if (documentConverter == null) {
            requireAbsolutePath(sourceRoot);
            requireAbsolutePath(targetRoot);
            documentConverter = new FreeMarkerDocumentConverter(filesystem, createDocumentLoader(sourceRoot), targetRoot);
        }
        return documentConverter;
    }

    @Override
    public ActionFactory createActionFactory(Path sourceRoot, Path targetRoot) {
        if (actionFactory == null) {
            requireAbsolutePath(sourceRoot);
            requireAbsolutePath(targetRoot);
            actionFactory = new RealActionFactory(filesystem, createDocumentConverter(sourceRoot, targetRoot));
        }
        return actionFactory;
    }
}

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

import nl.ulso.magisto.action.Action;
import nl.ulso.magisto.action.ActionCallback;
import nl.ulso.magisto.action.ActionSet;
import nl.ulso.magisto.converter.DocumentConverter;
import nl.ulso.magisto.io.FileSystem;
import nl.ulso.magisto.io.PathFilter;
import nl.ulso.magisto.loader.DocumentLoader;
import nl.ulso.magisto.sitemap.Sitemap;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.SortedSet;

import static nl.ulso.magisto.io.Paths.prioritizeOnExtension;
import static nl.ulso.magisto.sitemap.Sitemap.emptySitemap;

/**
 * Knits all the components in the Magisto system together (like a module) and runs it.
 */
class Magisto {

    static final String INDEX_FILE_NAME = "index";

    private final boolean forceCopy;
    private final MagistoFactoryBuilder magistoFactoryBuilder;
    private final FileSystem fileSystem;

    public Magisto(boolean forceOverwrite, MagistoFactoryBuilder magistoFactoryBuilder) {
        this.forceCopy = forceOverwrite;
        this.magistoFactoryBuilder = magistoFactoryBuilder;
        this.fileSystem = magistoFactoryBuilder.getFileSystem();
    }

    /*
    Runs Magisto by first determining a set of actions to perform, and then actually performing them, keeping
    statistics along the way.

    The actions are collected and sorted in a specific manner, so that they are performed in the right order. For
    example: deletions from the target first, in reverse order (first files in directories, then the directories
    themselves). Copies go later, in lexicographical order. That's one reasons why collecting actions and performing
    them are two distinct steps. Also, I like this more.
     */
    public Statistics run(final String sourceDirectory, final String targetDirectory) throws IOException {
        final Statistics statistics = new Statistics();
        try {
            statistics.begin();
            final Path sourceRoot = fileSystem.resolveSourceDirectory(sourceDirectory);
            final Path targetRoot = fileSystem.prepareTargetDirectory(targetDirectory);
            final MagistoFactory magistoFactory = magistoFactoryBuilder
                    .withSourceRoot(sourceRoot)
                    .withTargetRoot(targetRoot)
                    .build();

            final Sitemap currentSitemap = loadCurrentSitemap(targetRoot);

            final ActionSet actions = new ActionSet(magistoFactory.createActionFactory());
            addSourceActions(actions, magistoFactory, forceCopy || currentSitemap.isEmpty());
            addStaticActions(actions, magistoFactory);

            final Sitemap updatedSitemap = currentSitemap.apply(actions.computeChanges(),
                    magistoFactory.createDocumentLoader());

            actions.performAll(new ActionCallback() {
                @Override
                public void actionPerformed(Action action) {
                    statistics.registerActionPerformed(action);
                }
            });

            updatedSitemap.save(fileSystem, targetRoot);
            fileSystem.writeTouchFile(targetRoot);
        } finally {
            statistics.end();
        }
        return statistics;
    }

    private Sitemap loadCurrentSitemap(Path targetRoot) {
        if (forceCopy) {
            return emptySitemap();
        }
        try {
            return Sitemap.load(fileSystem, targetRoot);
        } catch (IOException e) {
            return emptySitemap();
        }
    }

    /*
    If it weren't for files that can disappear from the source and must therefore be removed from the target,
    determining the list of actions could be as simple as selecting all files in the source directory that are newer
    than the last export. In that case detecting the files to be deleted would require a separate step after performing
    actions on the source files, to detect all files in the target directory that weren't updated. This balanced line
    algorithm is simpler. It's a bit faster too.
     */
    private void addSourceActions(ActionSet actions, MagistoFactory magistoFactory, boolean forceOverwrite)
            throws IOException {
        final Path sourceRoot = magistoFactory.getSourceRoot();
        final Path targetRoot = magistoFactory.getTargetRoot();

        final DocumentLoader documentLoader = magistoFactory.createDocumentLoader();
        final DocumentConverter documentConverter = magistoFactory.createDocumentConverter();
        final boolean forceConvert = forceOverwrite || documentConverter.isCustomTemplateChanged();

        final SortedSet<Path> sourcePaths = findSourcePaths(sourceRoot, documentLoader);
        final Iterator<Path> sources = sourcePaths.iterator();
        final Iterator<Path> targets = findTargetPaths(targetRoot,
                new SkipGeneratedIndexFilesFilter(sourcePaths, documentLoader, documentConverter),
                documentConverter).iterator();

        Path source = nullableNext(sources);
        Path target = nullableNext(targets);
        while (source != null || target != null) {
            final int comparison = compareNullablePaths(source, target, documentLoader, documentConverter);

            if (comparison == 0) { // Corresponding source and target
                if (isSourceNewerThanTarget(sourceRoot.resolve(source), targetRoot.resolve(target))) {
                    if (documentLoader.supports(source)) {
                        actions.addConvertSourceAction(source);
                    } else {
                        actions.addCopySourceAction(source);
                    }
                } else if (forceConvert && documentLoader.supports(source)) {
                    actions.addConvertSourceAction(source);
                } else if (forceCopy) {
                    actions.addCopySourceAction(source);
                } else {
                    actions.addSkipSourceAction(source);
                }
                source = nullableNext(sources);
                target = nullableNext(targets);

            } else if (comparison < 0) { // Source exists, no corresponding target
                if (documentLoader.supports(source)) {
                    actions.addConvertSourceAction(source);
                } else {
                    actions.addCopySourceAction(source);
                }
                source = nullableNext(sources);

            } else if (comparison > 0) { // Target exists, no corresponding source
                actions.addDeleteTargetAction(target);
                target = nullableNext(targets);
            }
        }
    }

    private SortedSet<Path> findSourcePaths(Path sourceRoot, DocumentLoader documentLoader) throws IOException {
        return fileSystem.findAllPaths(sourceRoot, prioritizeOnExtension(documentLoader.getSupportedExtensions()));
    }

    private SortedSet<Path> findTargetPaths(Path targetRoot, PathFilter pathFilter,
                                            DocumentConverter documentConverter) throws IOException {
        return fileSystem.findAllPaths(targetRoot, pathFilter,
                prioritizeOnExtension(documentConverter.getTargetExtension()));
    }

    private void addStaticActions(ActionSet actions, MagistoFactory magistoFactory) throws IOException {
        final Path staticRoot = magistoFactory.getStaticRoot();

        if (fileSystem.notExists(staticRoot)) {
            return;
        }

        final Path targetRoot = magistoFactory.getTargetRoot();
        final SortedSet<Path> staticPaths = fileSystem.findAllPaths(staticRoot);
        for (Path staticPath : staticPaths) {
            final Path targetPath = targetRoot.resolve(staticPath);
            if (forceCopy || fileSystem.notExists(targetPath)
                    || isSourceNewerThanTarget(staticRoot.resolve(staticPath), targetPath)) {
                actions.addCopyStaticAction(staticPath);
            } else {
                actions.addSkipStaticAction(staticPath);
            }
        }
    }

    private Path nullableNext(Iterator<Path> paths) {
        return paths.hasNext() ? paths.next() : null;
    }

    private int compareNullablePaths(Path source, Path target, DocumentLoader documentLoader,
                                     DocumentConverter documentConverter) {
        if (source == null) {
            return 1;
        }
        if (target == null) {
            return -1;
        }
        if (documentLoader.supports(source)) {
            return documentConverter.getConvertedFileName(source).compareTo(target);
        }
        return source.compareTo(target);
    }

    private boolean isSourceNewerThanTarget(Path sourcePath, Path targetPath) throws IOException {
        final long sourceLastModified = fileSystem.getLastModifiedInMillis(sourcePath);
        final long targetLastModified = fileSystem.getLastModifiedInMillis(targetPath);
        return sourceLastModified > targetLastModified;
    }
}

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
import nl.ulso.magisto.action.ActionFactory;
import nl.ulso.magisto.action.ActionSet;
import nl.ulso.magisto.converter.FileConverter;
import nl.ulso.magisto.converter.FileConverterFactory;
import nl.ulso.magisto.io.FileSystemAccessor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * Knits all the components in the Magisto system together (like a module) and runs it.
 */
class Magisto {
    private static final String STATIC_CONTENT_DIRECTORY = ".static";

    private final boolean forceCopy;
    private final FileSystemAccessor fileSystemAccessor;
    private final ActionFactory actionFactory;
    private final FileConverterFactory fileConverterFactory;

    public Magisto(boolean forceOverwrite, FileSystemAccessor fileSystemAccessor, ActionFactory actionFactory,
                   FileConverterFactory fileConverterFactory) {
        this.forceCopy = forceOverwrite;
        this.fileSystemAccessor = fileSystemAccessor;
        this.actionFactory = actionFactory;
        this.fileConverterFactory = fileConverterFactory;
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
            final Path sourceRoot = fileSystemAccessor.resolveSourceDirectory(sourceDirectory);
            final Path targetRoot = fileSystemAccessor.prepareTargetDirectory(targetDirectory);
            fileSystemAccessor.requireDistinct(sourceRoot, targetRoot);

            final ActionSet actions = new ActionSet(actionFactory);
            addSourceActions(actions, sourceRoot, targetRoot);
            addStaticActions(actions, sourceRoot, targetRoot);

            actions.performAll(fileSystemAccessor, sourceRoot, targetRoot, new ActionCallback() {
                @Override
                public void actionPerformed(Action action) {
                    statistics.registerActionPerformed(action);
                }
            });

            fileSystemAccessor.writeTouchFile(targetRoot);
        } finally {
            statistics.end();
        }
        return statistics;
    }

    /*
    If it weren't for files that can disappear from the source and must therefore be removed from the target,
    determining the list of actions could be as simple as selecting all files in the source directory that are newer
    than the last export. In that case detecting the files to be deleted would require a separate step after performing
    actions on the source files, to detect all files in the target directory that weren't updated. This balanced line
    algorithm is simpler. It's a bit faster too.
     */
    private void addSourceActions(ActionSet actions, Path sourceRoot, Path targetRoot) throws IOException {
        final FileConverter fileConverter = fileConverterFactory.create(fileSystemAccessor, sourceRoot);
        final boolean forceConvert = forceCopy
                || fileConverter.isCustomTemplateChanged(fileSystemAccessor, sourceRoot, targetRoot);
        final Iterator<Path> sources = fileSystemAccessor.findAllPaths(sourceRoot).iterator();
        final Iterator<Path> targets = fileSystemAccessor.findAllPaths(targetRoot).iterator();

        Path source = nullableNext(sources);
        Path target = nullableNext(targets);
        while (source != null || target != null) {
            final int comparison = compareNullablePaths(source, target, fileConverter);

            if (comparison == 0) { // Corresponding source and target
                if (isSourceNewerThanTarget(sourceRoot.resolve(source), targetRoot.resolve(target))) {
                    if (fileConverter.supports(source)) {
                        actions.addConvertSourceAction(source, fileConverter);
                    } else {
                        actions.addCopySourceAction(source);
                    }
                } else if (forceConvert && fileConverter.supports(source)) {
                    actions.addConvertSourceAction(source, fileConverter);
                } else if (forceCopy) {
                    actions.addCopySourceAction(source);
                } else {
                    actions.addSkipSourceAction(source);
                }
                source = nullableNext(sources);
                target = nullableNext(targets);

            } else if (comparison < 0) { // Source exists, no corresponding target
                if (fileConverter.supports(source)) {
                    actions.addConvertSourceAction(source, fileConverter);
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

    private void addStaticActions(ActionSet actions, Path sourceRoot, Path targetRoot) throws IOException {
        final Path staticRoot = sourceRoot.resolve(STATIC_CONTENT_DIRECTORY);
        if (fileSystemAccessor.notExists(staticRoot)) {
            return;
        }
        final SortedSet<Path> staticPaths = fileSystemAccessor.findAllPaths(staticRoot);
        for (Path staticPath : staticPaths) {
            final Path targetPath = targetRoot.resolve(staticPath);
            if (forceCopy || fileSystemAccessor.notExists(targetPath)
                    || isSourceNewerThanTarget(staticRoot.resolve(staticPath), targetPath)) {
                actions.addCopyStaticAction(staticPath, STATIC_CONTENT_DIRECTORY);
            } else {
                actions.addSkipStaticAction(staticPath);
            }
        }
    }

    private Path nullableNext(Iterator<Path> paths) {
        return paths.hasNext() ? paths.next() : null;
    }

    private int compareNullablePaths(Path source, Path target, FileConverter fileConverter) {
        if (source == null) {
            return 1;
        }
        if (target == null) {
            return -1;
        }
        if (fileConverter.supports(source)) {
            return fileConverter.getConvertedFileName(source).compareTo(target);
        }
        return source.compareTo(target);
    }

    private boolean isSourceNewerThanTarget(Path sourcePath, Path targetPath) throws IOException {
        final long sourceLastModified = fileSystemAccessor.getLastModifiedInMillis(sourcePath);
        final long targetLastModified = fileSystemAccessor.getLastModifiedInMillis(targetPath);
        return sourceLastModified > targetLastModified;
    }
}

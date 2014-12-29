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

package nl.ulso.magisto.action;

import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;

import static nl.ulso.magisto.action.ActionCategory.SOURCE;
import static nl.ulso.magisto.action.ActionType.COPY_SOURCE;
import static nl.ulso.magisto.io.Paths.requireAbsolutePath;

/**
 * Copies a file from the source root to the target root.
 */
class CopySourceAction extends AbstractAction {

    private final FileSystem fileSystem;
    private final Path sourceRoot;
    private final Path targetRoot;

    CopySourceAction(FileSystem fileSystem, Path sourceRoot, Path targetRoot, Path path) {
        super(path, SOURCE);
        this.fileSystem = fileSystem;
        this.sourceRoot = requireAbsolutePath(sourceRoot);
        this.targetRoot = requireAbsolutePath(targetRoot);
    }

    @Override
    public ActionType getActionType() {
        return COPY_SOURCE;
    }

    @Override
    public void perform() throws IOException {
        fileSystem.copy(sourceRoot,targetRoot, getPath());
    }
}

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

package nl.ulso.magisto.action;

import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;

import static nl.ulso.magisto.action.ActionCategory.SOURCE;
import static nl.ulso.magisto.action.ActionType.DELETE_TARGET;
import static nl.ulso.magisto.io.Paths.requireAbsolutePath;

/**
 * Deletes a file or directory from the target root
 */
class DeleteTargetAction extends AbstractAction {

    private final FileSystem fileSystem;
    private final Path targetRoot;

    DeleteTargetAction(FileSystem fileSystem, Path targetRoot, Path path) {
        super(path, SOURCE);
        this.fileSystem = fileSystem;
        this.targetRoot = requireAbsolutePath(targetRoot);
    }

    @Override
    public ActionType getActionType() {
        return DELETE_TARGET;
    }

    @Override
    public void perform() throws IOException {
        fileSystem.delete(targetRoot.resolve(getPath()));
    }
}

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

import nl.ulso.magisto.document.DocumentConverter;
import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.nio.file.Path;

import static nl.ulso.magisto.action.ActionCategory.SOURCE;

/**
 * Converts a file in the source root to a different format in the target root in the same directory.
 */
class ConvertSourceAction extends AbstractAction {

    private final DocumentConverter documentConverter;

    ConvertSourceAction(Path path, DocumentConverter documentConverter) {
        super(path, SOURCE);
        this.documentConverter = documentConverter;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.CONVERT_SOURCE;
    }

    @Override
    public void perform(FileSystem fileSystem, Path sourceRoot, Path targetRoot) throws IOException {
        documentConverter.convert(getPath());
    }
}

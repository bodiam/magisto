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

import nl.ulso.magisto.converter.FileConverter;

import java.nio.file.Path;

/**
 * Real implementation of the {@link ActionFactory}.
 */
public class RealActionFactory implements ActionFactory {
    @Override
    public Action skipSource(Path path) {
        return new SkipSourceAction(path);
    }

    @Override
    public Action skipStatic(Path path) {
        return new SkipStaticAction(path);
    }

    @Override
    public Action copySource(Path path) {
        return new CopySourceAction(path);
    }

    @Override
    public Action copyStatic(Path path, String staticContentDirectory) {
        return new CopyStaticAction(path, staticContentDirectory);
    }

    @Override
    public Action convertSource(Path path, FileConverter fileConverter) {
        return new ConvertSourceAction(path, fileConverter);
    }

    @Override
    public Action deleteTarget(Path path) {
        return new DeleteTargetAction(path);
    }
}
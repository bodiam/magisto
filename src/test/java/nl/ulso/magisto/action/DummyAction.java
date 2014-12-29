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

import java.nio.file.Path;

public class DummyAction implements Action {

    private final DummyActionFactory factory;
    private final Path path;
    private final ActionCategory category;
    private final ActionType type;

    public DummyAction(DummyActionFactory factory, Path path, ActionCategory category, ActionType type) {
        this.factory = factory;
        this.path = path;
        this.category = category;
        this.type = type;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public ActionCategory getActionCategory() {
        return category;
    }

    @Override
    public ActionType getActionType() {
        return type;
    }

    @Override
    public void perform() {
        factory.registerActionPerformed(this);
    }

    @Override
    public String toString() {
        return String.format("%s: %s", type, path);
    }
}

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

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static nl.ulso.magisto.action.ActionCategory.SOURCE;
import static nl.ulso.magisto.action.ActionCategory.STATIC;
import static nl.ulso.magisto.action.ActionType.*;

public class DummyActionFactory implements ActionFactory {
    private final Map<ActionType, Integer> counts = new HashMap<>();

    @Override
    public Action skipSource(Path path) {
        return new DummyAction(this, path, SOURCE, SKIP_SOURCE);
    }

    @Override
    public Action skipStatic(Path path) {
        return new DummyAction(this, path, STATIC, SKIP_STATIC);
    }

    @Override
    public Action copySource(Path path) {
        return new DummyAction(this, path, SOURCE, COPY_SOURCE);
    }

    @Override
    public Action copyStatic(Path path, String staticContentDirectory) {
        return new DummyAction(this, path, STATIC, COPY_STATIC);
    }

    @Override
    public Action convertSource(Path path, DocumentConverter documentConverter) {
        return new DummyAction(this, path, SOURCE, CONVERT_SOURCE);
    }

    @Override
    public Action deleteTarget(Path path) {
        return new DummyAction(this, path, SOURCE, DELETE_TARGET);
    }

    public void registerActionPerformed(DummyAction action) {
        final ActionType type = action.getActionType();
        if (!counts.containsKey(type)) {
            counts.put(type, 1);
        } else {
            counts.put(type, 1 + counts.get(type));
        }
    }

    public int countFor(ActionType type) {
        if (counts.containsKey(type)) {
            return counts.get(type);
        }
        return 0;
    }
}

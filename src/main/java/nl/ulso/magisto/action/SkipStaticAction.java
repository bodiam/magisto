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

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static nl.ulso.magisto.action.ActionCategory.STATIC;
import static nl.ulso.magisto.action.ActionType.SKIP_STATIC;

/**
 * Represents a no-op action, for a path that is skipped.
 */
class SkipStaticAction extends AbstractAction {

    SkipStaticAction(Path path) {
        super(path, STATIC);
    }

    @Override
    public ActionType getActionType() {
        return SKIP_STATIC;
    }

    @Override
    public void perform() throws IOException {
        Logger.getGlobal().log(FINE, String.format("Skipping static '%s'. No changes detected.", getPath()));
    }
}

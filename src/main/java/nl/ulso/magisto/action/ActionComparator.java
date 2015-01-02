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

import java.util.Comparator;

/**
 * Comparator for {@link Action}s. It puts actions in order of type. If actions are of the same type, they are ordered
 * lexicographically on path, except for {@link DeleteTargetAction}s. These are ordered in reverse.
 */
class ActionComparator implements Comparator<Action> {

    private final Class[] actionClasses = new Class[]{
            SkipSourceAction.class,
            DeleteTargetAction.class,
            CopySourceAction.class,
            ConvertSourceAction.class,
            SkipStaticAction.class,
            CopyStaticAction.class
    };

    @Override
    public int compare(Action action1, Action action2) {
        for (Class actionClass : actionClasses) {
            if (actionClass.isInstance(action1) && !actionClass.isInstance(action2)) {
                return -1;
            }
            if (actionClass.isInstance(action2) && !actionClass.isInstance(action1)) {
                return 1;
            }
        }
        if (action1 instanceof DeleteTargetAction) {
            return action2.getPath().compareTo(action1.getPath());
        }
        return action1.getPath().compareTo(action2.getPath());
    }
}

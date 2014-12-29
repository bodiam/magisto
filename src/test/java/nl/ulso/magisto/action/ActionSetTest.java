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

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;

public class ActionSetTest {

    @Test
    public void testAddActions() throws Exception {
        final ActionSet actions = new ActionSet(new DummyActionFactory());
        actions.addCopySourceAction(createPath("source1"));
        actions.addCopySourceAction(createPath("source2"));
        final List<Action> performed = performActions(actions);
        assertEquals(2, performed.size());
    }

    @Test
    public void testStaticCopyReplacesSourceDelete() throws Exception {
        final ActionSet actions = new ActionSet(new DummyActionFactory());
        actions.addDeleteTargetAction(createPath("file"));
        actions.addCopyStaticAction(createPath("file"));
        final List<Action> performed = performActions(actions);
        assertEquals(1, performed.size());
        assertEquals(ActionType.COPY_STATIC, performed.get(0).getActionType());
    }

    @Test
    public void testStaticSkipReplacesSourceDelete() throws Exception {
        final ActionSet actions = new ActionSet(new DummyActionFactory());
        actions.addSkipStaticAction(createPath("file"));
        actions.addDeleteTargetAction(createPath("file"));
        final List<Action> performed = performActions(actions);
        assertEquals(1, performed.size());
        assertEquals(ActionType.SKIP_STATIC, performed.get(0).getActionType());
    }

    @Test
    public void testSourceCopyReplacesStaticCopy() throws Exception {
        final ActionSet actions = new ActionSet(new DummyActionFactory());
        actions.addCopyStaticAction(createPath("file"));
        actions.addCopySourceAction(createPath("file"));
        final List<Action> performed = performActions(actions);
        assertEquals(1, performed.size());
        assertEquals(ActionType.COPY_SOURCE, performed.get(0).getActionType());
    }

    @Test
    public void testSourceSkipReplacesStaticCopy() throws Exception {
        final ActionSet actions = new ActionSet(new DummyActionFactory());
        actions.addSkipSourceAction(createPath("file"));
        actions.addCopyStaticAction(createPath("file"));
        final List<Action> performed = performActions(actions);
        assertEquals(1, performed.size());
        assertEquals(ActionType.SKIP_SOURCE, performed.get(0).getActionType());
    }

    @Test(expected = IllegalStateException.class)
    public void testPerformedActionsCannotBePerformedAgain() throws Exception {
        final ActionSet actions = new ActionSet(new DummyActionFactory());
        actions.addCopySourceAction(createPath("file"));
        final List<Action> performed = performActions(actions);
        performed.get(0).perform();
    }

    private List<Action> performActions(ActionSet actions) throws IOException {
        final List<Action> performed = new ArrayList<>();
        actions.performAll(new ActionCallback() {
            @Override
            public void actionPerformed(Action action) {
                performed.add(action);
            }
        });
        return performed;
    }
}
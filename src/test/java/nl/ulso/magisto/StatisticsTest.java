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

import nl.ulso.magisto.action.ActionCategory;
import nl.ulso.magisto.action.ActionType;
import nl.ulso.magisto.action.DummyAction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static nl.ulso.magisto.action.ActionCategory.SOURCE;
import static nl.ulso.magisto.action.ActionCategory.STATIC;
import static nl.ulso.magisto.action.ActionType.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class StatisticsTest {

    private LogCapturingHandler logCapturingHandler;

    @Before
    public void setUp() throws Exception {
        logCapturingHandler = new LogCapturingHandler();
        logCapturingHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format("%s%n", record.getMessage());
            }
        });
        Logger.getLogger("").addHandler(logCapturingHandler);
    }

    @After
    public void tearDown() throws Exception {
        Logger.getLogger("").removeHandler(logCapturingHandler);
    }

    @Test(expected = IllegalStateException.class)
    public void testBeginMayBeCalledOnlyOnce() throws Exception {
        new Statistics().begin().begin();
    }

    @Test(expected = IllegalStateException.class)
    public void testEndCannotBeCalledWithoutBegin() throws Exception {
        new Statistics().end();
    }

    @Test(expected = IllegalStateException.class)
    public void testPrintCannotBeCalledWithoutEnd() throws Exception {
        new Statistics().begin().log();
    }

    @Test(expected = IllegalStateException.class)
    public void testActionPerformedCannotBeCalledBeforeStart() throws Exception {
        new Statistics().registerActionPerformed(createAction(SOURCE, COPY_SOURCE));
    }

    @Test(expected = IllegalStateException.class)
    public void testActionPerformedCannotBeCalledAfterEnd() throws Exception {
        new Statistics().begin().end().registerActionPerformed(createAction(SOURCE, COPY_SOURCE));
    }

    @Test
    public void testSuccessfulRun() throws Exception {
        new Statistics()
                .begin()
                .registerActionPerformed(createAction(SOURCE, COPY_SOURCE))
                .registerActionPerformed(createAction(SOURCE, DELETE_TARGET))
                .registerActionPerformed(createAction(STATIC, COPY_STATIC))
                .registerActionPerformed(createAction(SOURCE, SKIP_SOURCE))
                .registerActionPerformed(createAction(STATIC, COPY_STATIC))
                .registerActionPerformed(createAction(STATIC, COPY_STATIC))
                .registerActionPerformed(createAction(STATIC, COPY_STATIC))
                .registerActionPerformed(createAction(SOURCE, CONVERT_SOURCE))
                .registerActionPerformed(createAction(SOURCE, CONVERT_SOURCE))
                .registerActionPerformed(createAction(SOURCE, SKIP_SOURCE))
                .registerActionPerformed(createAction(STATIC, SKIP_STATIC))
                .registerActionPerformed(createAction(SOURCE, COPY_SOURCE))
                .registerActionPerformed(createAction(SOURCE, COPY_SOURCE))
                .end()
                .log();
        String log = logCapturingHandler.getLog();
        assertThat(log, containsString("Done!"));
        assertThat(log, containsString("Copied 3 source"));
        assertThat(log, containsString("Copied 4 static"));
        assertThat(log, containsString("Converted 2 source"));
        assertThat(log, containsString("Deleted 1 target"));
        assertThat(log, containsString("Skipped 2 source"));
        assertThat(log, containsString("Skipped 1 static"));
    }

    private DummyAction createAction(ActionCategory category, ActionType type) {
        return new DummyAction(null, null, category, type);
    }

    private class LogCapturingHandler extends Handler {

        private final StringBuilder builder = new StringBuilder();

        @Override
        public void publish(LogRecord record) {
            builder.append(getFormatter().formatMessage(record));
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }

        public String getLog() {
            return builder.toString();
        }
    }
}
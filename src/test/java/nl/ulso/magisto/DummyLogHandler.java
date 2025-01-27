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

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

class DummyLogHandler extends Handler {

    private static DummyLogHandler handler;

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

    public static String getLog() {
        return handler.builder.toString();
    }

    public static void install() {
        handler = new DummyLogHandler();
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format("%s%n", record.getMessage());
            }
        });
        Logger.getLogger("").addHandler(handler);

    }

    public static void uninstall() {
        Logger.getLogger("").removeHandler(handler);
    }
}

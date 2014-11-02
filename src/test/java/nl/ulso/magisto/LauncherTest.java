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

import nl.ulso.magisto.action.DummyActionFactory;
import nl.ulso.magisto.converter.DummyFileConverterFactory;
import nl.ulso.magisto.io.DummyFileSystemAccessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.StandardErrorStreamLog;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LauncherTest {

    @Rule
    public final ExpectedSystemExit systemExit = ExpectedSystemExit.none();

    @Rule
    public final StandardErrorStreamLog errorLog = new StandardErrorStreamLog();

    @Rule
    public final StandardOutputStreamLog outputLog = new StandardOutputStreamLog();

    @Before
    public void setUp() throws Exception {
        DummyLogHandler.install();
    }

    @After
    public void tearDown() throws Exception {
        DummyLogHandler.uninstall();
    }

    @Test
    public void testNoProgramArguments() throws Exception {
        systemExit.expectSystemExitWithStatus(-1);
        Launcher.setDummyMagistoForTesting(new DummyMagisto());
        Launcher.main(new String[]{});
        assertThat(errorLog.getLog(), containsString("invalid arguments"));
        assertThat(outputLog.getLog(), is(""));
    }

    @Test
    public void testValidProgramArguments() throws Exception {
        Launcher.setDummyMagistoForTesting(new DummyMagisto());
        Launcher.main(new String[]{"-t", "foo"});
        assertThat(errorLog.getLog(), is(""));
        assertThat(outputLog.getLog(), is(""));
        assertThat(DummyLogHandler.getLog(), containsString("Done!"));
    }

    @Test
    public void testIOExceptionDuringRun() throws Exception {
        systemExit.expectSystemExitWithStatus(-1);
        Launcher.setDummyMagistoForTesting(new DummyMagistoWithIOException());
        Launcher.main(new String[]{"-t", "foo"});
        assertThat(errorLog.getLog(), containsString("Oops!"));
        assertThat(errorLog.getLog(), containsString("--expected--"));
        assertThat(outputLog.getLog(), is(""));
    }

    @Test
    public void testSourceDirectoryNotDefined() throws Exception {
        final Options options = Launcher.parseProgramOptions(new String[]{"-t", "foo"});
        final String sourceDirectory = Launcher.resolveSourceDirectory(options);
        assertThat(sourceDirectory, is(System.getProperty("user.dir")));
    }

    @Test
    public void testSourceDirectoryDefined() throws Exception {
        final Options options = Launcher.parseProgramOptions(new String[]{"-s", "bar", "-t", "foo"});
        final String sourceDirectory = Launcher.resolveSourceDirectory(options);
        assertThat(sourceDirectory, is("bar"));
    }

    @Test
    public void testDefaultMagistoCreation() throws Exception {
        Launcher.setDummyMagistoForTesting(null);
        final Magisto magisto = Launcher.createMagisto(false, null);
        assertNotNull(magisto);
    }

    @Test
    public void testNormalLogging() throws Exception {
        Launcher.configureLoggingSystem(false);
        Logger.getGlobal().log(Level.FINE, "FINE");
        Logger.getGlobal().log(Level.INFO, "INFO");
        final String log = DummyLogHandler.getLog();
        assertThat(log, not(containsString("FINE")));
        assertThat(log, containsString("INFO"));
    }

    @Test
    public void testVerboseLogging() throws Exception {
        Launcher.configureLoggingSystem(true);
        Logger.getGlobal().log(Level.FINE, "FINE");
        Logger.getGlobal().log(Level.INFO, "INFO");
        final String log = DummyLogHandler.getLog();
        assertThat(log, containsString("FINE"));
        assertThat(log, containsString("INFO"));
    }

    private static final class DummyMagisto extends Magisto {
        private DummyMagisto() {
            super(false, new DummyFileSystemAccessor(), new DummyActionFactory(), new DummyFileConverterFactory());
        }

        @Override
        public Statistics run(String sourceDirectory, String targetDirectory) throws IOException {
            return new Statistics().begin().end();
        }
    }

    private static final class DummyMagistoWithIOException extends Magisto {
        private DummyMagistoWithIOException() {
            super(false, new DummyFileSystemAccessor(), new DummyActionFactory(), new DummyFileConverterFactory());
        }

        @Override
        public Statistics run(String sourceDirectory, String targetDirectory) throws IOException {
            throw new IOException("--expected--");
        }
    }
}
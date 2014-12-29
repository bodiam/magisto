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

import nl.ulso.magisto.action.RealActionFactory;
import nl.ulso.magisto.document.freemarker.FreeMarkerDocumentConverterFactory;
import nl.ulso.magisto.document.markdown.MarkdownDocumentLoader;
import nl.ulso.magisto.git.DummyGitClient;
import nl.ulso.magisto.git.GitClient;
import nl.ulso.magisto.git.JGitClient;
import nl.ulso.magisto.io.RealFileSystem;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Runs Magisto as a Maven plugin.
 */
@Mojo(name = "export", requiresProject = false)
public class MagistoMojo extends AbstractMojo {

    private final Handler logHandler = new MavenLogHandler();

    @Parameter(property = "source", defaultValue = ".")
    private String sourceDirectory;

    @Parameter(property = "target", required = true)
    private String targetDirectory;

    @Parameter(property = "force", defaultValue = "false")
    private boolean forceOverwrite;

    @Parameter(property = "verbose", defaultValue = "false")
    private boolean verbose;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Handler consoleHandler = configureLogging(verbose);
        final GitClient gitClient = createGitClient(sourceDirectory);
        final RealFileSystem fileSystem = new RealFileSystem();
        final Magisto magisto = new Magisto(forceOverwrite, fileSystem, new RealActionFactory(),
                new MarkdownDocumentLoader(fileSystem), new FreeMarkerDocumentConverterFactory(gitClient));
        try {
            magisto.run(sourceDirectory, targetDirectory).log();
        } catch (IOException e) {
            throw new MojoFailureException("IOException occurred", e);
        } finally {
            resetLogging(consoleHandler);
        }
    }

    private GitClient createGitClient(String sourceDirectory) throws MojoFailureException {
        try {
            return new JGitClient(sourceDirectory);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.INFO, "No Git repository found. Version information will not be available.");
            return new DummyGitClient();
        }
    }

    private Handler configureLogging(boolean verbose) {
        final Level level = verbose ? Level.FINEST : Level.INFO;
        final Logger rootLogger = Logger.getLogger("");
        final Handler consoleHandler = rootLogger.getHandlers()[0];
        rootLogger.removeHandler(consoleHandler);
        rootLogger.addHandler(logHandler);
        rootLogger.setLevel(level);
        logHandler.setLevel(level);
        return consoleHandler;
    }

    private void resetLogging(Handler consoleHandler) {
        final Logger rootLogger = Logger.getLogger("");
        rootLogger.removeHandler(logHandler);
        rootLogger.addHandler(consoleHandler);
    }

    private class MavenLogHandler extends Handler {

        @Override
        public void publish(LogRecord record) {
            final Log log = MagistoMojo.this.getLog();
            final String message = record.getMessage();
            final Level level = record.getLevel();
            if (log.isInfoEnabled() && (level == Level.INFO || level == Level.FINE)) {
                log.info(message);
            } else if (log.isDebugEnabled() && (level == Level.FINER || level == Level.FINEST)) {
                log.debug(message);
            } else if (log.isWarnEnabled() && level == Level.WARNING) {
                log.warn(message);
            } else if (log.isErrorEnabled() && level == Level.SEVERE) {
                log.error(message);
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }
    }
}

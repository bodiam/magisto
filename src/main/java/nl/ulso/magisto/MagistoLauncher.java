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

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.ValidationFailure;
import nl.ulso.magisto.git.GitClient;
import nl.ulso.magisto.git.GitClientStub;
import nl.ulso.magisto.git.JGitClient;
import nl.ulso.magisto.io.RealFileSystem;

import java.io.IOException;
import java.util.logging.*;

/**
 * Launches the Magisto application.
 * <p>
 * This is the only place where:
 * <ul>
 * <li>Program arguments are parsed</li>
 * <li>Output is generated directly to System.out</li>
 * <li>Output is generated directly to System.err</li>
 * <li>The {@link Magisto} class is instantiated/configured and run</li>
 * </ul>
 * </p>
 */
public class MagistoLauncher {

    private static final String WORKING_DIRECTORY = System.getProperty("user.dir");

    private static Magisto DUMMY_MAGISTO = null; // For testing

    public static void main(String[] arguments) {
        try {
            final Options options = parseProgramOptions(arguments);
            configureLoggingSystem(options.isVerbose());
            final String sourceDirectory = resolveSourceDirectory(options);
            final GitClient gitClient = createGitClient(sourceDirectory);
            final Magisto magisto = createMagisto(options.isForceOverwrite(), gitClient);
            run(magisto, sourceDirectory, options.getTargetDirectory());
        } catch (RuntimeException e) {
            System.exit(-1);
        }
    }

    static void configureLoggingSystem(boolean verbose) {
        final Logger rootLogger = Logger.getLogger("");
        final Level level = verbose ? Level.FINEST : Level.INFO;
        final Handler handler = rootLogger.getHandlers()[0];
        rootLogger.setLevel(level);
        handler.setLevel(level);
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format("%s%n", record.getMessage());
            }
        });
    }

    static Options parseProgramOptions(String[] arguments) {
        try {
            return CliFactory.parseArguments(Options.class, arguments);
        } catch (ArgumentValidationException e) {
            System.err.println("You gave me one or more invalid arguments: ");
            for (ValidationFailure failure : e.getValidationFailures()) {
                System.err.println(failure);
            }
            throw new RuntimeException(e);
        }
    }

    private static GitClient createGitClient(String sourceDirectory) {
        try {
            return new JGitClient(sourceDirectory);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.INFO, "No Git repository found. Version information will not be available.");
            return new GitClientStub();
        }
    }

    static Magisto createMagisto(boolean forceOverwrite, GitClient gitClient) {
        if (DUMMY_MAGISTO != null) {
            return DUMMY_MAGISTO;
        }
        final RealFileSystem fileSystem = new RealFileSystem();
        final RealMagistoFactory magistoFactory = new RealMagistoFactory(fileSystem, gitClient);
        return new Magisto(forceOverwrite, fileSystem, magistoFactory);
    }

    private static void run(Magisto magisto, String sourceDirectory, String targetDirectory) {
        final Statistics statistics;
        try {
            statistics = magisto.run(sourceDirectory, targetDirectory);
        } catch (Exception e) {
            System.err.println("Oops! An exception occurred...");
            System.err.println();
            System.err.println("Deliver the following to your friendly neighbourhood geek to help you out:");
            System.err.println();
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
        statistics.log();
    }

    static String resolveSourceDirectory(Options options) {
        final String sourceOption = options.getSourceDirectory();
        if (sourceOption != null) {
            return sourceOption;
        }
        return WORKING_DIRECTORY;
    }

    static void setDummyMagistoForTesting(Magisto dummyMagisto) {
        DUMMY_MAGISTO = dummyMagisto;
    }
}

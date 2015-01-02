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

package nl.ulso.magisto;

import com.lexicalscope.jewel.cli.Option;

/**
 * Represents the command-line options one can pass
 */
interface Options {

    @Option(shortName = "s", longName = "source", description = "Source directory, defaults to the current directory",
            defaultToNull = true)
    String getSourceDirectory();

    @Option(shortName = "t", longName = "target", description = "Target directory")
    String getTargetDirectory();

    @Option(shortName = "f", longName = "force", description = "Forces overwriting of files that would be skipped otherwise")
    boolean isForceOverwrite();

    @Option(shortName = "v", longName = "verbose", description = "Use verbose logging")
    boolean isVerbose();

    @Option(shortName = "h", longName = "help", description = "Show this help text", helpRequest = true)
    boolean isHelp();
}

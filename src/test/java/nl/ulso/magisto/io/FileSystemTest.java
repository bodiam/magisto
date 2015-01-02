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

package nl.ulso.magisto.io;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents a test case that is executed in a temporary directory. Implementations needn't care about creating the
 * temporary directory, nor about cleaning it up.
 *
 * @see nl.ulso.magisto.io.FileSystemTestRunner
 */
public interface FileSystemTest {
    boolean mustCreateTempDirectory();

    void prepareTempDirectory(Path path) throws IOException;

    void runTest(Path path) throws IOException;
}

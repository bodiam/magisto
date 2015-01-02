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

import nl.ulso.magisto.io.FileSystemTestWithEmptyTempDirectory;
import nl.ulso.magisto.io.FileSystemTestWithPreparedDirectory;
import nl.ulso.magisto.io.RealFileSystem;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static nl.ulso.magisto.io.FileSystemTestRunner.runFileSystemTest;
import static org.junit.Assert.assertTrue;

public class TouchFileTest {

    @Test
    public void testTouchFileIsWritten() throws Exception {
        runFileSystemTest(new FileSystemTestWithEmptyTempDirectory() {
            @Override
            public void runTest(Path path) throws IOException {
                new TouchFile(new RealFileSystem(), path).update();
                assertTrue(Files.exists(TouchFile.createTouchFilePath(path)));
            }
        });
    }

    @Test
    public void testTouchFileIsReplaced() throws Exception {
        runFileSystemTest(new FileSystemTestWithPreparedDirectory() {
            @Override
            public void prepareTempDirectory(Path path) throws IOException {
                new TouchFile(new RealFileSystem(), path).update();
            }

            @Override
            public void runTest(Path path) throws IOException {
                new TouchFile(new RealFileSystem(), path).update();
                assertTrue(Files.exists(TouchFile.createTouchFilePath(path)));
            }
        });
    }
}
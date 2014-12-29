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

import nl.ulso.magisto.io.DummyFileSystem;
import nl.ulso.magisto.io.DummyPathEntry;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;

import static nl.ulso.magisto.io.DummyPathEntry.createPathEntry;
import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;

public class DeleteTargetActionTest {

    private DummyFileSystem fileSystem;
    private Path targetRoot;

    @Before
    public void setUp() throws Exception {
        fileSystem = new DummyFileSystem();
        targetRoot = fileSystem.prepareTargetDirectory("target");
    }


    @Test
    public void testActionType() throws Exception {
        assertEquals(ActionType.DELETE_TARGET, new DeleteTargetAction(
                fileSystem, targetRoot, createPath("delete")).getActionType());
    }

    @Test
    public void testActionCategory() throws Exception {
        assertEquals(ActionCategory.SOURCE, new DeleteTargetAction(
                fileSystem, targetRoot, createPath("delete")).getActionCategory());
    }

    @Test
    public void testDelete() throws Exception {
        final DummyFileSystem fileSystem = new DummyFileSystem();
        final Path targetRoot = fileSystem.prepareTargetDirectory("target");
        final DummyPathEntry entry = createPathEntry("file");
        new DeleteTargetAction(fileSystem, targetRoot, entry.getPath()).perform();
        assertEquals("target:file", fileSystem.getLoggedDeletions());
    }
}
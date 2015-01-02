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

import nl.ulso.magisto.converter.DummyDocumentConverter;
import nl.ulso.magisto.io.DummyFileSystem;
import org.junit.Before;
import org.junit.Test;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertNotNull;

public class RealActionFactoryTest {

    private ActionFactory factory;

    @Before
    public void setUp() throws Exception {
        final DummyFileSystem fileSystem = new DummyFileSystem();
        factory = new RealActionFactory(fileSystem, new DummyDocumentConverter(fileSystem.getSourceRoot(),
                fileSystem.getTargetRoot()),
                fileSystem.getStaticRoot());

    }

    @Test
    public void testSkipSourceAction() throws Exception {
        assertNotNull(factory.skipSource(createPath("skip")));
    }

    @Test
    public void testSkipStaticAction() throws Exception {
        assertNotNull(factory.skipStatic(createPath("skip")));
    }

    @Test
    public void testCopySourceAction() throws Exception {
        assertNotNull(factory.copySource(createPath("copy")));
    }

    @Test
    public void testCopyStaticAction() throws Exception {
        assertNotNull(factory.copyStatic(createPath("copy")));
    }

    @Test
    public void testConvertAction() throws Exception {
        assertNotNull(factory.convertSource(createPath("convert")));
    }

    @Test
    public void testDeleteTargetAction() throws Exception {
        assertNotNull(factory.deleteTarget(createPath("delete")));
    }

}
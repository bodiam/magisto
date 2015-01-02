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
import nl.ulso.magisto.io.DummyPathEntry;
import org.junit.Before;
import org.junit.Test;

import static nl.ulso.magisto.io.DummyPathEntry.createPathEntry;
import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;

public class ConvertSourceActionTest {

    private DummyDocumentConverter fileConverter;

    @Before
    public void setUp() throws Exception {
        fileConverter = new DummyDocumentConverter(createPath("source"), createPath("target"), false);
    }

    @Test
    public void testActionType() throws Exception {
        assertEquals(ActionType.CONVERT_SOURCE,
                new ConvertSourceAction(fileConverter, createPath("convert")).getActionType());
    }

    @Test
    public void testActionCategory() throws Exception {
        assertEquals(ActionCategory.SOURCE,
                new ConvertSourceAction(fileConverter, createPath("convert")).getActionCategory());
    }

    @Test
    public void testCopy() throws Exception {
        final DummyPathEntry entry = createPathEntry("file.convert");
        new ConvertSourceAction(fileConverter, entry.getPath()).perform();
        assertEquals("source:file.convert -> target:file.converted", fileConverter.getLoggedConversions());
    }
}
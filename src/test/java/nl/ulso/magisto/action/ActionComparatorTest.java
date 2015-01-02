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

import java.nio.file.Path;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;

public class ActionComparatorTest {

    private ActionComparator comparator;
    private DummyFileSystem fileSystem;
    private Path sourceRoot;
    private Path targetRoot;

    @Before
    public void setUp() throws Exception {
        comparator = new ActionComparator();
        fileSystem = new DummyFileSystem();
        sourceRoot = fileSystem.getSourceRoot();
        targetRoot = fileSystem.getTargetRoot();
    }

    @Test
    public void testSkipBeforeDelete() throws Exception {
        final SkipSourceAction skip = new SkipSourceAction(createPath("a"));
        final DeleteTargetAction delete = new DeleteTargetAction(fileSystem, targetRoot, createPath("a"));
        assertEquals(-1, comparator.compare(skip, delete));
        assertEquals(1, comparator.compare(delete, skip));
    }

    @Test
    public void testSkipBeforeCopy() throws Exception {
        final SkipSourceAction skip = new SkipSourceAction(createPath("a"));
        final CopySourceAction copy = new CopySourceAction(fileSystem, sourceRoot, targetRoot, createPath("a"));
        assertEquals(-1, comparator.compare(skip, copy));
        assertEquals(1, comparator.compare(copy, skip));
    }

    @Test
    public void testSkipBeforeConvert() throws Exception {
        final SkipSourceAction skip = new SkipSourceAction(createPath("a"));
        final ConvertSourceAction convert = new ConvertSourceAction(new DummyDocumentConverter(), createPath("a"));
        assertEquals(-1, comparator.compare(skip, convert));
        assertEquals(1, comparator.compare(convert, skip));
    }

    @Test
    public void testDeleteBeforeCopy() throws Exception {
        final DeleteTargetAction delete = new DeleteTargetAction(fileSystem, targetRoot, createPath("a"));
        final CopySourceAction copy = new CopySourceAction(fileSystem, sourceRoot, targetRoot, createPath("a"));
        assertEquals(-1, comparator.compare(delete, copy));
        assertEquals(1, comparator.compare(copy, delete));
    }

    @Test
    public void testDeleteBeforeConvert() throws Exception {
        final DeleteTargetAction delete = new DeleteTargetAction(fileSystem, targetRoot, createPath("a"));
        final ConvertSourceAction convert = new ConvertSourceAction(new DummyDocumentConverter(), createPath("a"));
        assertEquals(-1, comparator.compare(delete, convert));
        assertEquals(1, comparator.compare(convert, delete));
    }

    @Test
    public void testCopyBeforeConvert() throws Exception {
        final ConvertSourceAction convert = new ConvertSourceAction(null, createPath("a"));
        final CopySourceAction copy = new CopySourceAction(fileSystem, sourceRoot, targetRoot, createPath("a"));
        assertEquals(-1, comparator.compare(copy, convert));
        assertEquals(1, comparator.compare(convert, copy));
    }

    @Test
    public void testSkipOrderedLexicographically() throws Exception {
        final SkipSourceAction skip1 = new SkipSourceAction(createPath("a"));
        final SkipSourceAction skip2 = new SkipSourceAction(createPath("b"));
        assertEquals(-1, comparator.compare(skip1, skip2));
        assertEquals(1, comparator.compare(skip2, skip1));
    }

    @Test
    public void testCopyOrderedLexicographically() throws Exception {
        final CopySourceAction copy1 = new CopySourceAction(fileSystem, sourceRoot, targetRoot, createPath("a"));
        final CopySourceAction copy2 = new CopySourceAction(fileSystem, sourceRoot, targetRoot, createPath("b"));
        assertEquals(-1, comparator.compare(copy1, copy2));
        assertEquals(1, comparator.compare(copy2, copy1));
    }

    @Test
    public void testDeleteOrderedLexicographicallyReversed() throws Exception {
        final DeleteTargetAction delete1 = new DeleteTargetAction(fileSystem, targetRoot, createPath("a"));
        final DeleteTargetAction delete2 = new DeleteTargetAction(fileSystem, targetRoot, createPath("b"));
        assertEquals(1, comparator.compare(delete1, delete2));
        assertEquals(-1, comparator.compare(delete2, delete1));
    }

    @Test
    public void testConvertOrderedLexicographically() throws Exception {
        final ConvertSourceAction convert1 = new ConvertSourceAction(new DummyDocumentConverter(), createPath("a"));
        final ConvertSourceAction convert2 = new ConvertSourceAction(new DummyDocumentConverter(), createPath("b"));
        assertEquals(-1, comparator.compare(convert1, convert2));
        assertEquals(1, comparator.compare(convert2, convert1));
    }
}
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

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertTrue;

public class SourcePathComparatorTest {

    private Comparator<Path> comparator;

    @Before
    public void setUp() throws Exception {
        comparator = new SourcePathComparator(new HashSet<>(Arrays.asList("md", "markdown")));
    }

    @Test
    public void testUnrelatedPathsAreSortedInOrder() throws Exception {
        final Path path1 = createPath("src", "main", "java", "Main.java");
        final Path path2 = createPath("src", "test", "java", "MainTest.java");
        assertTrue(comparator.compare(path1, path2) < 0);
    }

    @Test
    public void testPathToConvertBeforeOtherPaths1() throws Exception {
        final Path path1 = createPath("file.jpeg");
        final Path path2 = createPath("file.md");
        assertTrue(comparator.compare(path1, path2) > 0);
    }

    @Test
    public void testPathToConvertBeforeOtherPaths2() throws Exception {
        final Path path1 = createPath("file.md");
        final Path path2 = createPath("file.jpeg");
        assertTrue(comparator.compare(path1, path2) < 0);
    }
}
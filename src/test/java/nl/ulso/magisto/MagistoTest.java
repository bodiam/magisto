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

import nl.ulso.magisto.action.DummyActionFactory;
import nl.ulso.magisto.io.DummyFileSystem;
import nl.ulso.magisto.io.DummyPathEntry;
import nl.ulso.magisto.sitemap.SitemapTest;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static nl.ulso.magisto.action.ActionType.*;
import static nl.ulso.magisto.io.DummyPathEntry.createPathEntry;
import static org.junit.Assert.assertEquals;

public class MagistoTest {

    private DummyFileSystem fileSystem;
    private DummyMagistoFactoryBuilder magistoFactoryBuilder;
    private Magisto magisto;

    @Before
    public void setUp() throws Exception {
        magistoFactoryBuilder = new DummyMagistoFactoryBuilder();
        fileSystem = (DummyFileSystem) magistoFactoryBuilder.getFileSystem();
        magisto = new Magisto(false, magistoFactoryBuilder);
    }

    @Test
    public void testEmptySourceEmptyTarget() throws Exception {
        runTest(0, 0, 0, 0, 0, 0);
    }

    @Test
    public void testNormalFileEmptyTargetDirectory() throws Exception {
        fileSystem.addSourcePaths(createPathEntry("file.jpg"));
        runTest(0, 1, 0, 0, 0, 0);
    }

    @Test
    public void testConversionFileEmptyTargetDirectory() throws Exception {
        fileSystem.addSourcePaths(createPathEntry("file.convert"));
        runTest(0, 0, 1, 0, 0, 0);
    }

    @Test
    public void testUnknownFileInTargetDirectory() throws Exception {
        fileSystem.addTargetPaths(createPathEntry("file.jpg"));
        runTest(0, 0, 0, 1, 0, 0);
    }

    @Test
    public void testStaticFileEmptyTargetDirectory() throws Exception {
        fileSystem.addStaticPaths(createPathEntry("file.jpg"));
        runTest(0, 0, 0, 0, 0, 1);
    }

    @Test
    public void testNormalFilesExistNoChangesDetected() throws Exception {
        final DummyPathEntry file1 = createPathEntry("foo.txt");
        final DummyPathEntry file2 = createPathEntry("bar.jpg");
        fileSystem.addSourcePaths(file1, file2);
        fileSystem.addTargetPaths(file1, file2);
        runTest(2, 0, 0, 0, 0, 0);
    }

    @Test
    public void testStaticFilesExistNoChangesDetected() throws Exception {
        final DummyPathEntry file1 = createPathEntry("foo.txt");
        final DummyPathEntry file2 = createPathEntry("bar.jpg");
        fileSystem.addStaticPaths(file1, file2);
        fileSystem.addTargetPaths(file1, file2);
        runTest(0, 0, 0, 0, 2, 0);
    }

    @Test
    public void testMultipleSourceAndTargetFiles() throws Exception {
        // Prepare a basic sitemap, because not having it implies a forced convert
        fileSystem.registerTextFileForBufferedReader(".magisto-sitemap", SitemapTest.SITEMAP_JSON);
        prepareMultipleSourceAndTargetFiles();
        runTest(
                3, // sameFile1, sameFile2, foo.convert/foo.convert.converted
                1, // baz.txt
                1, // bar.convert
                1, // deleteTarget.me
                1, // .static/favicon.ico
                1  // .static/image.jpg
        );
    }

    @Test
    public void testMultipleSourceAndTargetFilesWithDetectedOverwrite() throws Exception {
        prepareMultipleSourceAndTargetFiles();
        magistoFactoryBuilder.setCustomTemplateChanged();
        magisto = new Magisto(false, magistoFactoryBuilder);
        runTest(
                2, // sameFile1, sameFile2
                1, // baz.txt
                2, // foo.convert/foo.convert.converted, bar.convert
                1, // deleteTarget.me
                1, // .static/favicon.ico
                1  // .static/image.jpg
        );
    }

    @Test
    public void testMultipleSourceAndTargetFilesWithForcedOverwrite() throws Exception {
        magisto = new Magisto(true, magistoFactoryBuilder);
        prepareMultipleSourceAndTargetFiles();
        runTest(
                0, // no skips: forced overwrite
                3, // sameFile1, sameFile2, baz.txt
                2, // foo.convert/foo.convert.converted, bar.convert
                1, // deleteTarget.me
                0, // no skips: forced overwrite
                2  // .static/favicon.ico, .static/image.jpg
        );
    }

    private void prepareMultipleSourceAndTargetFiles() throws InterruptedException {
        final DummyPathEntry sameSourceFile1 = createPathEntry("foo.txt");
        final DummyPathEntry sameSourceFile2 = createPathEntry("bar.jpg");
        final DummyPathEntry sameStaticFile = createPathEntry("favicon.ico");
        final DummyPathEntry nonChangedConversionFile = createPathEntry("foo.convert");
        fileSystem.addTargetPaths(
                sameSourceFile1,
                sameSourceFile2,
                sameStaticFile,
                createPathEntry("baz.txt"),
                createPathEntry("foo.converted"),
                createPathEntry("delete.me")
        );
        TimeUnit.SECONDS.sleep(1);
        fileSystem.addSourcePaths(
                sameSourceFile1,
                sameSourceFile2,
                createPathEntry("baz.txt"), // Same path, different timestamp: this one is newer
                nonChangedConversionFile,
                createPathEntry("bar.convert")
        );
        fileSystem.addStaticPaths(
                sameStaticFile,
                createPathEntry("image.jpg")
        );

    }

    @Test
    public void testSameFileNameDifferentExtensions() throws Exception {
        fileSystem.addTargetPaths(
                createPathEntry("test.convers"),
                createPathEntry("test.converted")
        );
        TimeUnit.SECONDS.sleep(1);
        fileSystem.addSourcePaths(
                createPathEntry("test.convers"),
                createPathEntry("test.convert")
        );
        runTest(0, 1, 1, 0, 0, 0);
    }

    private void runTest(int expectedSourceSkips, int expectedSourceCopies, int expectedSourceConversions,
                         int expectedTargetDeletions, int expectedStaticSkips, int expectedStaticCopies)
            throws Exception {
        final Statistics statistics = magisto.run("source", "target");
        System.out.println(statistics);
        // Number of actions performed must match up:
        final DummyActionFactory actionFactory = magistoFactoryBuilder.getDummyActionFactory();
        assertEquals(expectedSourceSkips, actionFactory.countFor(SKIP_SOURCE));
        assertEquals(expectedSourceCopies, actionFactory.countFor(COPY_SOURCE));
        assertEquals(expectedSourceConversions, actionFactory.countFor(CONVERT_SOURCE));
        assertEquals(expectedTargetDeletions, actionFactory.countFor(DELETE_TARGET));
        assertEquals(expectedStaticSkips, actionFactory.countFor(SKIP_STATIC));
        assertEquals(expectedStaticCopies, actionFactory.countFor(COPY_STATIC));
        // Statistics must match the number of actions performed
        assertEquals(expectedSourceSkips, statistics.countFor(SKIP_SOURCE));
        assertEquals(expectedSourceCopies, statistics.countFor(COPY_SOURCE));
        assertEquals(expectedSourceConversions, statistics.countFor(CONVERT_SOURCE));
        assertEquals(expectedTargetDeletions, statistics.countFor(DELETE_TARGET));
        assertEquals(expectedStaticSkips, statistics.countFor(SKIP_STATIC));
        assertEquals(expectedStaticCopies, statistics.countFor(COPY_STATIC));
    }
}

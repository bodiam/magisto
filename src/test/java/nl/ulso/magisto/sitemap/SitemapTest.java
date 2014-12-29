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

package nl.ulso.magisto.sitemap;

import nl.ulso.magisto.io.DummyFileSystem;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SitemapTest {

    public static final String SITEMAP_JSON = "{\"pages\":[" +
            "{\"title\":\"Test 1\",\"directory\":\"dir1\",\"file\":\"file1\"}," +
            "{\"title\":\"Test 2\",\"directory\":\"dir2\",\"file\":\"file2\"}" +
            "],\"version\":1}";

    @Test
    public void testWrite() throws Exception {
        final List<Page> pages = new ArrayList<>();
        pages.add(new Page("dir1", "file1", "Test 1"));
        pages.add(new Page("dir2", "file2", "Test 2"));
        final Sitemap sitemap = new Sitemap(pages);
        DummyFileSystem fileSystem = new DummyFileSystem();
        sitemap.save(fileSystem, fileSystem.prepareTargetDirectory(""));
        assertEquals(SITEMAP_JSON, fileSystem.getTextFileFromBufferedWriter(".magisto-sitemap"));
    }

    @Test
    public void testRead() throws Exception {
        DummyFileSystem fileSystem = new DummyFileSystem();
        fileSystem.registerTextFileForBufferedReader(".magisto-sitemap", SITEMAP_JSON);
        final Sitemap sitemap = Sitemap.load(fileSystem, fileSystem.prepareTargetDirectory(""));
        assertEquals(2, sitemap.getPages().size());
        final Page page1 = sitemap.getPages().get(0);
        assertEquals("Test 1", page1.getTitle());
        assertEquals("dir1", page1.getDirectory());
        assertEquals("file1", page1.getFile());
    }

    @Test(expected = IOException.class)
    public void testReadIncompatibleVersion() throws Exception {
        DummyFileSystem fileSystem = new DummyFileSystem();
        final String incompatibleSitemap = SITEMAP_JSON.replace("1}", "2}");
        fileSystem.registerTextFileForBufferedReader(".magisto-sitemap", incompatibleSitemap);
        Sitemap.load(fileSystem, fileSystem.prepareTargetDirectory(""));
    }
}
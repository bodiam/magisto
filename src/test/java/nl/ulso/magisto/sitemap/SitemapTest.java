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

package nl.ulso.magisto.sitemap;

import nl.ulso.magisto.action.Change;
import nl.ulso.magisto.action.ChangeType;
import nl.ulso.magisto.converter.DummyDocumentConverter;
import nl.ulso.magisto.io.DummyFileSystem;
import nl.ulso.magisto.loader.DummyDocumentLoader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SitemapTest {

    public static final String SITEMAP_JSON = "{\n" +
            "  \"root\": {\n" +
            "    \"directories\": [\n" +
            "      {\n" +
            "        \"directories\": [],\n" +
            "        \"name\": \"dir1\",\n" +
            "        \"pages\": [\n" +
            "          {\n" +
            "            \"title\": \"Test 1\",\n" +
            "            \"filename\": \"file1.converted\"\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"directories\": [],\n" +
            "        \"name\": \"dir2\",\n" +
            "        \"pages\": [\n" +
            "          {\n" +
            "            \"title\": \"Test 2\",\n" +
            "            \"filename\": \"file2.converted\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ],\n" +
            "    \"name\": \".\",\n" +
            "    \"pages\": []\n" +
            "  },\n" +
            "  \"version\": 1\n" +
            "}";

    @Test
    public void testWrite() throws Exception {
        final DummyFileSystem fileSystem = new DummyFileSystem();
        final Sitemap sitemap = new Sitemap(fileSystem, fileSystem.getTargetRoot());
        final Directory rootDirectory = sitemap.getRootDirectory();
        rootDirectory.updatePage(createPath("dir1", "file1.converted"), "Test 1");
        rootDirectory.updatePage(createPath("dir2", "file2.converted"), "Test 2");
        sitemap.save();
        final String text = fileSystem.getTextFileFromBufferedWriter(".magisto-sitemap");
        System.out.println(text);
        final JSONObject document = (JSONObject) new JSONParser().parse(text);
        final Long version = (Long) document.get("version");
        assertThat(version, is(1l));
        final JSONObject root = (JSONObject) document.get("root");
        assertNotNull(root);
    }

    @Test
    public void testRead() throws Exception {
        final DummyFileSystem fileSystem = new DummyFileSystem();
        fileSystem.registerTextFileForBufferedReader(".magisto-sitemap", SITEMAP_JSON);
        final Sitemap sitemap = Sitemap.load(fileSystem, fileSystem.getTargetRoot());
        final Directory rootDirectory = sitemap.getRootDirectory();
        assertTrue(rootDirectory.getPages().isEmpty());
        assertThat(rootDirectory.getSubdirectories().size(), is(2));
        for (Directory directory : rootDirectory.getSubdirectories()) {
            assertTrue(directory.getSubdirectories().isEmpty());
            assertThat(directory.getPages().size(), is(1));
        }
    }

    @Test(expected = IOException.class)
    public void testReadIncompatibleVersion() throws Exception {
        final DummyFileSystem fileSystem = new DummyFileSystem();
        final String incompatibleSitemap = SITEMAP_JSON.replace("\"version\": 1", "\"version\": 2");
        fileSystem.registerTextFileForBufferedReader(".magisto-sitemap", incompatibleSitemap);
        Sitemap.load(fileSystem, fileSystem.getTargetRoot());
    }

    @Test
    public void testApplyChanges() throws Exception {
        final DummyFileSystem fileSystem = new DummyFileSystem();
        fileSystem.registerTextFileForBufferedReader(".magisto-sitemap", SITEMAP_JSON);
        final Sitemap sitemap = Sitemap.load(fileSystem, fileSystem.getTargetRoot());
        final List<Change> changes = new ArrayList<>();
        changes.add(new Change(ChangeType.INSERT_OR_UPDATE, createPath("dir1", "file3.convert"))); // Add
        changes.add(new Change(ChangeType.DELETE, createPath("dir1").resolve("file2.convert"))); // Delete
        changes.add(new Change(ChangeType.INSERT_OR_UPDATE, createPath("dir3").resolve("file1.convert"))); // Add
        final Sitemap newSitemap = sitemap.applyChanges(changes, new DummyDocumentLoader(
                fileSystem.getSourceRoot()), new DummyDocumentConverter());

        final Directory rootDirectory = newSitemap.getRootDirectory();
        assertThat(rootDirectory.getSubdirectories().size(), is(3));

        final Directory dir1 = findSubdirectory(rootDirectory, "dir1");
        assertTrue(dir1.isChanged());
        assertThat(dir1.getPages().size(), is(2)); // One added, one deleted

        final Directory dir2 = findSubdirectory(rootDirectory, "dir2");
        assertFalse(dir2.isChanged());
        assertThat(dir2.getPages().size(), is(1)); // No changes

        final Directory dir3 = findSubdirectory(rootDirectory, "dir3");
        assertThat(dir3.getPages().size(), is(1)); // Added
        assertTrue(dir3.isChanged());
    }

    private Directory findSubdirectory(Directory rootDirectory, String name) {
        for (Directory directory : rootDirectory.getSubdirectories()) {
            if (directory.getName().equals(name)) {
                return directory;
            }
        }
        throw new IllegalStateException("Oops");
    }
}
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

import nl.ulso.magisto.io.Paths;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import java.nio.file.Path;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DirectoryTest {

    @Test
    public void testNewDirectory() throws Exception {
        final Directory directory = new Directory("dir");
        assertThat(directory.getName(), is("dir"));
        assertThat(directory.isEmpty(), is(true));
    }

    @Test
    public void testNestedDirectories() throws Exception {
        final Directory directory = new Directory(".");
        final Path filePath = createPath("deep", "path", "to", "file");
        directory.updatePage(filePath, "Test");
        final Directory parent1 = directory.getDirectory("deep").getDirectory("path").getDirectory("to");
        final Directory parent2 = directory.getDirectory(filePath);
        assertNotNull(parent1);
        assertNotNull(parent2);
        assertSame(parent1, parent2);
        assertThat(parent1.getPages().size(), is(1));
    }

    @Test
    public void testFromJSON() throws Exception {
        final Directory directory = Directory.fromJSONObject((JSONObject) new JSONParser().parse("{\n" +
                "  \"name\": \"parent\",\n" +
                "  \"directories\": [],\n" +
                "  \"pages\": []\n" +
                "}"));
        assertNotNull(directory);
    }

    @Test
    public void testToJSON() throws Exception {
        final Directory directory = new Directory("root");
        directory.updatePage(Paths.createPath("sub", "file"), "Title 1");
        directory.updatePage(Paths.createPath("file"), "Title 2");
        final JSONObject jsonObject = directory.toJSONObject();

        final JSONArray pages = (JSONArray) jsonObject.get("pages");
        assertNotNull(pages);
        assertThat(pages.size(), is(1));

        final JSONArray directories = ((JSONArray) jsonObject.get("directories"));
        assertNotNull(directories);
        assertThat(directories.size(), is(1));
    }
}
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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class PageTest {

    @Test
    public void testNewPage() throws Exception {
        final Page page = new Page("file", "title");
        assertThat(page.getFilename(), is("file"));
        assertThat(page.getTitle(), is("title"));
    }

    @Test
    public void testFromJSON() throws Exception {
        final Page page = Page.fromJSONObject((JSONObject) new JSONParser().parse("{\n" +
                "  \"filename\": \"foo\",\n" +
                "  \"title\": \"bar\"\n" +
                "}"));
        assertNotNull(page);
        assertThat(page.getFilename(), is("foo"));
        assertThat(page.getTitle(), is("bar"));
    }

    @Test
    public void testToJSON() throws Exception {
        final Page page = new Page("foo", "bar");
        final JSONObject object = page.toJSONObject();
        assertThat(((String) object.get("filename")), is("foo"));
        assertThat(((String) object.get("title")), is("bar"));
    }
}
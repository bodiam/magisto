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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single page in the sitemap.
 */
public class Page implements Comparable<Page> {

    private static final String FILENAME_FIELD = "filename";
    private static final String TITLE_FIELD = "title";

    private final String filename;
    private final String title;

    Page(String filename) {
        this(filename, "");
    }

    Page(String filename, String title) {
        this.filename = filename;
        this.title = title;
    }

    @Override
    public int compareTo(Page page) {
        return filename.compareTo(page.filename);
    }

    static Page fromJSONObject(JSONObject object) {
        return new Page(
                (String) object.get(FILENAME_FIELD),
                (String) object.get(TITLE_FIELD));
    }

    JSONObject toJSONObject() {
        Map<String, String> map = new HashMap<>();
        map.put(FILENAME_FIELD, filename);
        map.put(TITLE_FIELD, title);
        return new JSONObject(map);
    }

    public String getFilename() {
        return filename;
    }

    public String getTitle() {
        return title;
    }
}

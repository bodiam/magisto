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
import org.json.simple.JSONObject;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single page in the sitemap
 */
public class Page implements Comparable<Page> {

    private static final String PATH_FIELD = "path";
    private static final String TITLE_FIELD = "title";

    private final Path path;
    private final String title;

    Page(Path path) {
        this(path, "");
    }

    Page(Path path, String title) {
        this.path = path;
        this.title = title;
    }

    static Page fromJSONObject(JSONObject object) {
        return new Page(
                Paths.createPath((String) object.get(PATH_FIELD)),
                (String) object.get(TITLE_FIELD));
    }

    JSONObject toJSONObject() {
        Map<String, String> map = new HashMap<>();
        map.put(PATH_FIELD, path.toString());
        map.put(TITLE_FIELD, title);
        return new JSONObject(map);
    }

    @Override
    public int compareTo(Page page) {
        return path.compareTo(page.path);
    }

    public Path getPath() { return path; }

    public String getTitle() {
        return title;
    }
}

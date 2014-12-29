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

import org.json.simple.JSONObject;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single page in the sitemap
 */
public class Page implements Comparable<Page> {

    private static final String DIRECTORY_FIELD = "directory";
    private static final String FILE_FIELD = "file";
    private static final String TITLE_FIELD = "title";

    private final String directory;
    private final String file;
    private final String title;

    Page(Path path) {
        this(path, "");

    }

    Page(Path path, String title) {
        this(path.getParent() != null ? path.getParent().toString() : "", path.getFileName().toString(), title);
    }

    Page(String directory, String file, String title) {
        this.directory = directory;
        this.file = file;
        this.title = title;
    }

    static Page fromJSONObject(JSONObject object) {
        return new Page(
                (String) object.get(DIRECTORY_FIELD),
                (String) object.get(FILE_FIELD),
                (String) object.get(TITLE_FIELD));
    }

    JSONObject toJSONObject() {
        Map<String, String> map = new HashMap<>();
        map.put(DIRECTORY_FIELD, directory);
        map.put(FILE_FIELD, file);
        map.put(TITLE_FIELD, title);
        return new JSONObject(map);
    }

    @Override
    public int compareTo(Page page) {
        int comparison = directory.compareTo(page.directory);
        if (comparison != 0) {
            return comparison;
        }
        return file.compareTo(page.file);
    }

    public String getDirectory() {
        return directory;
    }

    public String getFile() {
        return file;
    }

    public String getTitle() {
        return title;
    }
}

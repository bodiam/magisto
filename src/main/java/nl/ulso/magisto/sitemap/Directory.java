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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.file.Path;
import java.util.*;

/**
 * Represents a single directory in the sitemap. A directory has subdirectories and/or pages.
 */
public class Directory implements Comparable<Directory> {

    private static final String NAME_FIELD = "name";
    private static final String DIRECTORIES_FIELD = "directories";
    private static final String PAGES_FIELD = "pages";

    private final String name;
    private final Map<String, Directory> subdirectories;
    private final Set<Page> pages;
    private boolean isChanged;

    Directory(final String name) {
        this.name = name;
        this.subdirectories = new HashMap<>();
        this.pages = new HashSet<>();
        this.isChanged = false;
    }

    Directory(String name, Map<String, Directory> subdirectories, Set<Page> pages) {
        this.name = name;
        this.subdirectories = new HashMap<>();
        for (Map.Entry<String, Directory> entry : subdirectories.entrySet()) {
            this.subdirectories.put(entry.getKey(), entry.getValue().deepCopy());
        }
        this.pages = new HashSet<>(pages);
        this.isChanged = false;
    }

    Directory deepCopy() {
        return new Directory(name, subdirectories, pages);
    }

    @Override
    public int compareTo(Directory directory) {
        return name.compareTo(directory.name);
    }

    static Directory fromJSONObject(JSONObject object) {
        final String name = (String) object.get(NAME_FIELD);
        final Map<String, Directory> subdirectories = new HashMap<>();
        final JSONArray directoryArray = ((JSONArray) object.get(DIRECTORIES_FIELD));
        for (Object directoryObject : directoryArray) {
            final Directory directory = Directory.fromJSONObject((JSONObject) directoryObject);
            subdirectories.put(directory.getName(), directory);
        }
        final Set<Page> pages = new HashSet<>();
        final JSONArray pageArray = ((JSONArray) object.get(PAGES_FIELD));
        for (Object pageObject : pageArray) {
            final Page page = Page.fromJSONObject((JSONObject) pageObject);
            pages.add(page);
        }
        return new Directory(name, subdirectories, pages);
    }

    JSONObject toJSONObject() {
        Map<String, Object> map = new HashMap<>();
        map.put(NAME_FIELD, name);
        final JSONArray directoryArray = new JSONArray();
        for (Directory directory : subdirectories.values()) {
            if (!directory.isEmpty()) {
                //noinspection unchecked
                directoryArray.add(directory.toJSONObject());
            }
        }
        map.put(DIRECTORIES_FIELD, directoryArray);
        final JSONArray pageArray = new JSONArray();
        for (Page page : pages) {
            //noinspection unchecked
            pageArray.add(page.toJSONObject());
        }
        map.put(PAGES_FIELD, pageArray);
        return new JSONObject(map);
    }

    void removePage(Path fullPath) {
        apply(fullPath, new PageFunction() {
            @Override
            public void apply(Directory directory, String filename) {
                directory.pages.remove(new Page(filename));
            }
        });
    }

    void updatePage(Path fullPath, final String title) {
        apply(fullPath, new PageFunction() {
            @Override
            public void apply(Directory directory, String filename) {
                final Page page = new Page(filename, title);
                directory.pages.remove(page);
                directory.pages.add(page);
            }
        });
    }

    private void apply(Path path, PageFunction function) {
        if (path.getNameCount() == 1) {
            isChanged = true;
            function.apply(this, path.toString());
        } else {
            final Directory subdirectory = findSubdirectory(resolveName(path));
            subdirectory.apply(resolveSubpath(path), function);
        }
    }

    private Directory findSubdirectory(String name) {
        if (!subdirectories.containsKey(name)) {
            subdirectories.put(name, new Directory(name));
        }
        return subdirectories.get(name);
    }

    public String getName() {
        return name;
    }

    public Set<Page> getPages() {
        return Collections.unmodifiableSet(pages);
    }

    public boolean isEmpty() {
        return subdirectories.isEmpty() && pages.isEmpty();
    }

    boolean isChanged() {
        return isChanged;
    }

    public Set<Directory> getSubdirectories() {
        return new HashSet<>(subdirectories.values());
    }

    public Directory getDirectory(Path path) {
        if (path.getNameCount() == 1) {
            return this;
        }
        return subdirectories.get(resolveName(path)).getDirectory(resolveSubpath(path));
    }

    public Directory getDirectory(String name) {
        return subdirectories.get(name);
    }

    private String resolveName(Path path) {
        return path.getName(0).toString();
    }

    private Path resolveSubpath(Path path) {
        return path.subpath(1, path.getNameCount());
    }

    private interface PageFunction {
        void apply(Directory directory, String filename);
    }
}

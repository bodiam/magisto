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

import nl.ulso.magisto.document.DocumentLoader;
import nl.ulso.magisto.io.FileSystem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.*;

import static java.util.Collections.unmodifiableSortedSet;
import static nl.ulso.magisto.io.Paths.requireAbsolutePath;

/**
 * Represents the map of all pages in the target site.
 */
public class Sitemap {

    /**
     * Name of the sitemap file in the target directory.
     */
    private static final String SITEMAP_FILE = ".magisto-sitemap";

    private static final String VERSION_FIELD = "version";
    private static final String PAGES_FIELD = "pages";

    // Whenever an incompatible change is introduced in the Sitemap structure, increase this number!
    private static final long CURRENT_VERSION = 1l;

    private final SortedSet<Page> pages;

    Sitemap(Set<Page> pages) {
        this.pages = unmodifiableSortedSet(new TreeSet<>(pages));
    }

    private Sitemap() {
        this(Collections.<Page>emptySet());
    }

    public static Sitemap emptySitemap() {
        return new Sitemap();
    }

    public static Sitemap load(FileSystem fileSystem, Path targetRoot) throws IOException {
        requireAbsolutePath(targetRoot);
        final Path path = targetRoot.resolve(SITEMAP_FILE);
        try (final Reader reader = fileSystem.newBufferedReaderForTextFile(path)) {
            final JSONObject document = (JSONObject) new JSONParser().parse(reader);
            final Long version = (Long) (document.get(VERSION_FIELD));
            if (version == null || version != CURRENT_VERSION) {
                throw new IOException("Invalid sitemap version: " + version);
            }
            final JSONArray pageArray = (JSONArray) document.get(PAGES_FIELD);
            final Set<Page> pages = new HashSet<>(pageArray.size());
            for (final Object object : pageArray) {
                pages.add(Page.fromJSONObject((JSONObject) object));
            }
            return new Sitemap(pages);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    public Set<Page> getPages() {
        return pages;
    }

    public void save(FileSystem fileSystem, Path targetRoot) throws IOException {
        requireAbsolutePath(targetRoot);
        final Path path = targetRoot.resolve(SITEMAP_FILE);
        final Map<String, Object> documentMap = new HashMap<>();
        documentMap.put(VERSION_FIELD, CURRENT_VERSION);
        final JSONArray pageArray = new JSONArray();
        for (final Page page : this.pages) {
            //noinspection unchecked
            pageArray.add(page.toJSONObject());
        }
        documentMap.put(PAGES_FIELD, pageArray);
        final JSONObject document = new JSONObject(documentMap);
        try (final Writer writer = fileSystem.newBufferedWriterForTextFile(path)) {
            document.writeJSONString(writer);
        }
    }

    /**
     * Applies all changes in the list to this sitemap and returns a new, updated sitemap.
     *
     * @param changes The list of changes to apply.
     * @return A new sitemap, based on the current one, with all changes applied.
     */
    public Sitemap apply(List<PageChange> changes, DocumentLoader documentLoader, Path sourceRoot) throws IOException {
        final Set<Page> pages = new HashSet<>(this.pages);
        for (PageChange change : changes) {
            final Path path = change.getPath();
            switch (change.getChangeType()) {
                case DELETE:
                    pages.remove(new Page(path));
                case INSERT_OR_UPDATE:
                    final Page page = new Page(path, documentLoader.loadDocument(sourceRoot.resolve(path)).getTitle());
                    pages.remove(page);
                    pages.add(page);
                    break;
            }
        }
        return new Sitemap(pages);
    }

    public boolean isEmpty() {
        return pages.isEmpty();
    }
}

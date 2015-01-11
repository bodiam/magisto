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
import nl.ulso.magisto.converter.DocumentConverter;
import nl.ulso.magisto.io.FileSystem;
import nl.ulso.magisto.loader.DocumentLoader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static final String ROOT_DIRECTORY_FIELD = "root";

    // Whenever an incompatible change is introduced in the Sitemap structure, increase this number!
    private static final long CURRENT_VERSION = 1l;

    private final FileSystem fileSystem;
    private final Path targetRoot;
    private final Directory rootDirectory;

    Sitemap(FileSystem fileSystem, Path targetRoot) {
        this(fileSystem, targetRoot, new Directory("."));
    }

    private Sitemap(FileSystem fileSystem, Path targetRoot, Directory rootDirectory) {
        this.fileSystem = fileSystem;
        this.targetRoot = targetRoot;
        this.rootDirectory = rootDirectory;
    }

    Sitemap(Sitemap original, List<Change> changes, DocumentLoader documentLoader, DocumentConverter documentConverter)
            throws IOException {
        this.fileSystem = original.fileSystem;
        this.targetRoot = original.targetRoot;
        this.rootDirectory = original.rootDirectory.deepCopy();
        Logger.getGlobal().log(Level.FINE, String.format("Detected %d changes to the existing sitemap.", changes.size()));
        for (Change change : changes) {
            final Path sourcePath = change.getPath();
            final Path targetPath = documentConverter.getConvertedFileName(sourcePath);
            switch (change.getChangeType()) {
                case DELETE:
                    this.rootDirectory.removePage(targetPath);
                    break;
                case INSERT_OR_UPDATE:
                    this.rootDirectory.updatePage(targetPath, documentLoader.loadDocument(sourcePath).getTitle());
                    break;
            }
        }
    }

    public static Sitemap emptySitemap(FileSystem fileSystem, Path targetRoot) {
        return new Sitemap(fileSystem, targetRoot);
    }

    /**
     * Applies all changes in the list to this sitemap.
     */
    public Sitemap applyChanges(List<Change> changes, DocumentLoader documentLoader, DocumentConverter documentConverter)
            throws IOException {
        return new Sitemap(this, changes, documentLoader, documentConverter);
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
            final JSONObject rootObject = (JSONObject) document.get(ROOT_DIRECTORY_FIELD);
            final Directory rootDirectory = Directory.fromJSONObject(rootObject);
            Logger.getGlobal().log(Level.FINE, String.format("Loaded an existing sitemap from %s.", SITEMAP_FILE));
            return new Sitemap(fileSystem, targetRoot, rootDirectory);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    public void save() throws IOException {
        final Path path = targetRoot.resolve(SITEMAP_FILE);
        final Map<String, Object> documentMap = new HashMap<>();
        documentMap.put(VERSION_FIELD, CURRENT_VERSION);
        documentMap.put(ROOT_DIRECTORY_FIELD, rootDirectory.toJSONObject());
        final JSONObject document = new JSONObject(documentMap);
        Logger.getGlobal().log(Level.FINE, String.format("Writing a new sitemap to %s.", SITEMAP_FILE));
        try (final Writer writer = fileSystem.newBufferedWriterForTextFile(path)) {
            document.writeJSONString(writer);
        }
    }

    public boolean isEmpty() {
        return rootDirectory.isEmpty();
    }

    Directory getRootDirectory() {
        return rootDirectory;
    }
}

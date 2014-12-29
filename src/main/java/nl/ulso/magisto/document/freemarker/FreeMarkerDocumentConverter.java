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

package nl.ulso.magisto.document.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import nl.ulso.magisto.document.DocumentConverter;
import nl.ulso.magisto.document.Document;
import nl.ulso.magisto.document.DocumentLoader;
import nl.ulso.magisto.git.GitClient;
import nl.ulso.magisto.io.FileSystem;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.ulso.magisto.io.Paths.splitOnExtension;

/**
 * Converts Markdown files to HTML using a FreeMarker template.
 */
class FreeMarkerDocumentConverter implements DocumentConverter {

    private static final String TEMPLATE_PATH = "/nl/ulso/magisto";
    private static final String DEFAULT_PAGE_TEMPLATE = "page_template.ftl";
    private static final String CUSTOM_PAGE_TEMPLATE = ".page.ftl";
    private static final String TARGET_EXTENSION = "html";

    private final FileSystem fileSystem;
    private final DocumentLoader documentLoader;
    private final GitClient gitClient;
    private final Template template;

    FreeMarkerDocumentConverter(FileSystem fileSystem, DocumentLoader documentLoader, Path sourceRoot,
                                GitClient gitClient) throws IOException {
        this.fileSystem = fileSystem;
        this.documentLoader = documentLoader;
        this.gitClient = gitClient;
        try {
            if (isCustomTemplateAvailable(sourceRoot)) {
                template = loadCustomTemplate(sourceRoot);
            } else {
                template = loadDefaultTemplate();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load built-in template", e);
        }
    }

    boolean isCustomTemplateAvailable(Path sourceRoot) {
        return fileSystem.exists(sourceRoot.resolve(CUSTOM_PAGE_TEMPLATE));
    }

    Template loadDefaultTemplate() throws IOException {
        Configuration configuration = createTemplateConfiguration();
        configuration.setClassForTemplateLoading(FreeMarkerDocumentConverter.class, TEMPLATE_PATH);
        return configuration.getTemplate(DEFAULT_PAGE_TEMPLATE);

    }

    Template loadCustomTemplate(Path sourceRoot) throws IOException {
        Configuration configuration = createTemplateConfiguration();
        configuration.setTemplateLoader(new CustomTemplateLoader(fileSystem, sourceRoot));
        return configuration.getTemplate(CUSTOM_PAGE_TEMPLATE);
    }

    private Configuration createTemplateConfiguration() {
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_21);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setDateTimeFormat("long");
        configuration.setSharedVariable("link", new LocalLinkRewriteDirective());
        return configuration;
    }

    @Override
    public boolean isCustomTemplateChanged(Path sourceRoot, Path targetRoot) throws IOException {
        if (isCustomTemplateAvailable(sourceRoot)) {
            final long touchFileTimestamp = fileSystem.getTouchFileLastModifiedInMillis(targetRoot);
            final long customTemplateTimestamp = fileSystem.getLastModifiedInMillis(
                    sourceRoot.resolve(CUSTOM_PAGE_TEMPLATE));
            return customTemplateTimestamp > touchFileTimestamp;
        }
        return false;
    }

    @Override
    public String getTargetExtension() {
        return TARGET_EXTENSION;
    }

    @Override
    public Path getConvertedFileName(Path path) {
        final Path pathWithoutExtension = splitOnExtension(path).getPathWithoutExtension();
        final String convertedFileName = pathWithoutExtension.getFileName().toString() + "." + TARGET_EXTENSION;
        return path.resolveSibling(convertedFileName);
    }

    @Override
    public void convert(Path sourceRoot, Path targetRoot, Path path) throws IOException {
        Logger.getGlobal().log(Level.FINE, String.format("Converting '%s' from Markdown to HTML.", path));
        final Path targetFile = targetRoot.resolve(getConvertedFileName(path));
        try (final Writer writer = fileSystem.newBufferedWriterForTextFile(targetFile)) {
            final Document document = documentLoader.loadDocument(sourceRoot.resolve(path));
            final Map<String, Object> model = createPageModel(path, document);
            template.process(model, writer);
        } catch (TemplateException e) {
            Logger.getGlobal().log(Level.SEVERE, String.format("There was a problem in your custom page template. " +
                    "All converted pages are probably broken! The cause: %s", e.getMessage()), e);
        }
    }

    Map<String, Object> createPageModel(Path path, Document document) {
        final Map<String, Object> model = new HashMap<>();
        model.put("timestamp", new Date());
        model.put("path", path);
        model.put("title", document.getTitle());
        model.put("content", document.toHtml());
        model.put("history", gitClient.getHistory(path));
        return model;
    }
}
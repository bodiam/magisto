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

package nl.ulso.magisto.converter.freemarker;

import freemarker.template.Template;
import nl.ulso.magisto.document.DummyHistory;
import nl.ulso.magisto.io.DummyFileSystem;
import nl.ulso.magisto.loader.DummyDocumentLoader;
import nl.ulso.magisto.loader.markdown.MarkdownDocument;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static nl.ulso.magisto.io.DummyPathEntry.createPathEntry;
import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.*;

public class FreeMarkerDocumentConverterTest {

    private FreeMarkerDocumentConverter fileConverter;
    private DummyFileSystem fileSystem;

    @Before
    public void setUp() throws Exception {
        this.fileSystem = new DummyFileSystem();
        this.fileConverter = new FreeMarkerDocumentConverter(fileSystem, new DummyDocumentLoader(
                fileSystem.getSourceRoot()), fileSystem.getTargetRoot());
    }

    @Test
    public void testConvertedFileNameAtx() throws Exception {
        assertEquals(createPath("foo.html"), fileConverter.getConvertedFileName(createPath("foo.MarkDown")));
    }

    @Test
    public void testCreatePageModel() throws Exception {
        final Date start = new Date();
        TimeUnit.SECONDS.sleep(1);
        final Map<String, Object> model = fileConverter.createPageModel(createPath("test.md"),
                new MarkdownDocument("# Title\n\nParagraph".toCharArray(), new DummyHistory()));
        TimeUnit.SECONDS.sleep(1);
        final Date end = new Date();
        final Date timestamp = (Date) model.get("timestamp");
        assertTrue(timestamp.after(start));
        assertTrue(timestamp.before(end));
        assertEquals("test.md", ((Path) model.get("path")).getFileName().toString());
        assertEquals("Title", model.get("title"));
        assertNotNull(model.get("content"));
        assertNotNull(model.get("history"));
    }

    @Test
    public void testConvertMarkdownFile() throws Exception {
        fileSystem.registerTextFileForBufferedReader("test.md", String.format("# Title%n%nParagraph"));
        fileConverter.convert(createPath("test.md"));
        final String output = fileSystem.getTextFileFromBufferedWriter("test.html");
        assertNotNull(output);
        System.out.println("output = " + output);
    }

    @Test
    public void testLoadDefaultTemplate() throws Exception {
        Template template = fileConverter.loadDefaultTemplate();
        assertNotNull(template);
        assertEquals("page_template.ftl", template.getName());
    }

    @Test
    public void testLoadCustomTemplate() throws Exception {
        fileSystem.addSourcePaths(createPathEntry(".page.ftl"));
        fileSystem.registerTextFileForBufferedReader(".page.ftl", "CUSTOM TEMPLATE");
        final Template template = fileConverter.loadCustomTemplate();
        assertNotNull(template);
        assertEquals(".page.ftl", template.getName());
    }

    @Test
    public void testConvertLocalLinkInTemplate() throws Exception {
        fileSystem.addSourcePaths(createPathEntry(".page.ftl"));
        fileSystem.registerTextFileForBufferedReader(".page.ftl", "<@link path=\"/static/favicon.ico\"/>");
        fileSystem.registerTextFileForBufferedReader("test.md", String.format("# Title%n%nParagraph"));
        this.fileConverter = new FreeMarkerDocumentConverter(fileSystem,
                new DummyDocumentLoader(fileSystem.getSourceRoot()), fileSystem.getTargetRoot());
        fileConverter.convert(createPath("dir", "test.md"));
        final String output = fileSystem.getTextFileFromBufferedWriter("test.html");
        assertEquals("../static/favicon.ico", output);
    }

    @Test
    public void testCustomTemplateHasNotChanged() throws Exception {
        fileSystem.addSourcePaths(createPathEntry(".page.ftl"));
        assertFalse(fileConverter.isCustomTemplateChanged());
    }

    @Test
    public void testCustomTemplateHasChanged() throws Exception {
        fileSystem.addTargetPaths(createPathEntry(".magisto-export"));
        MILLISECONDS.sleep(50);
        fileSystem.addSourcePaths(createPathEntry(fileSystem.getSourceRoot().resolve(".page.ftl")));
        fileSystem.registerTextFileForBufferedReader(".page.ftl", "CUSTOM TEMPLATE");
        assertTrue(fileConverter.isCustomTemplateChanged());
    }
}
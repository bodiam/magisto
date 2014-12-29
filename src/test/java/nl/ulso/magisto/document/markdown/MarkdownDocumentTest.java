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

package nl.ulso.magisto.document.markdown;

import nl.ulso.magisto.document.DummyHistory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MarkdownDocumentTest {

    private MarkdownDocument createMarkdownDocument(String text) {
        return new MarkdownDocument(text.toCharArray(), new DummyHistory());
    }

    @Test
    public void testTitleExtractionAtxHeader() throws Exception {
        assertEquals("title", createMarkdownDocument(
                "abstract\n\n# title\n\n## subtitle\n\nsome text").getTitle());
    }

    @Test
    public void testTitleExtractionSetextHeader() throws Exception {
        assertEquals("title", createMarkdownDocument(
                "abstract\n\ntitle\n=====\n\nsubtitle\n--------\n\nsome text").getTitle());
    }

    @Test
    public void testTitleExtractionNoTitlePresent() throws Exception {
        assertEquals("", createMarkdownDocument("some text").getTitle());
    }

    @Test
    public void testNormalFileLink() throws Exception {
        final String html = createMarkdownDocument("[link](image.jpg)").toHtml();
        assertEquals("<p><a href=\"image.jpg\">link</a></p>", html);
    }

    @Test
    public void testExternalLink() throws Exception {
        final String html = createMarkdownDocument("[link](http://www.github.com/voostindie/magisto)").toHtml();
        assertEquals("<p><a href=\"http://www.github.com/voostindie/magisto\">link</a></p>", html);
    }

    @Test
    public void testMarkdownFileLink() throws Exception {
        final String html = createMarkdownDocument("[link](file.md)").toHtml();
        assertEquals("<p><a href=\"file.html\">link</a></p>", html);
    }

    @Test
    public void testExternalMarkdownLink() throws Exception {
        final String html = createMarkdownDocument("[link](http://www.example.com/file.md)").toHtml();
        assertEquals("<p><a href=\"http://www.example.com/file.md\">link</a></p>", html);
    }

    @Test
    public void testMarkdownFileReferenceLink() throws Exception {
        final String html = createMarkdownDocument(String.format("[link][id]%n%n[id]: file.md")).toHtml();
        assertEquals("<p><a href=\"file.html\">link</a></p>", html);
    }


}
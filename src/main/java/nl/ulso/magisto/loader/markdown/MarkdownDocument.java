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

package nl.ulso.magisto.loader.markdown;

import nl.ulso.magisto.document.Document;
import nl.ulso.magisto.document.History;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.RootNode;

/**
 * Represents a Markdown document.
 */
public class MarkdownDocument implements Document {

    private static final ThreadLocal<PegDownProcessor> PROCESSOR = new ThreadLocal<PegDownProcessor>() {
        @Override
        protected PegDownProcessor initialValue() {
            return new PegDownProcessor(Extensions.ALL - Extensions.HARDWRAPS);
        }
    };

    private final RootNode rootNode;
    private final String title;
    private final History history;

    public MarkdownDocument(char[] markdownText, History history) {
        this.history = history;
        rootNode = PROCESSOR.get().parseMarkdown(markdownText);
        title = new TitleFinder().extractTitle(rootNode);
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String toHtml() {
        return new ToHtmlSerializer(new MarkdownLinkRenderer()).toHtml(rootNode);
    }

    @Override
    public History getHistory() {
        return history;
    }
}

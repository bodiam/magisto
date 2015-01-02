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

package nl.ulso.magisto.loader.markdown;

import org.parboiled.common.StringUtils;
import org.pegdown.LinkRenderer;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.RefLinkNode;

import static nl.ulso.magisto.loader.markdown.MarkdownDocumentLoader.MARKDOWN_EXTENSIONS;
import static org.pegdown.FastEncoder.encode;

/**
 * Link renderer that converts links to Markdown files to links to their equivalent HTML files.
 */
class MarkdownLinkRenderer extends LinkRenderer {

    @Override
    public Rendering render(ExpLinkNode node, String text) {
        Rendering rendering = new Rendering(resolveLink(node.url), text);
        return StringUtils.isEmpty(node.title) ? rendering : rendering.withAttribute("title", encode(node.title));
    }

    @Override
    public Rendering render(RefLinkNode node, String url, String title, String text) {
        Rendering rendering = new Rendering(resolveLink(url), text);
        return StringUtils.isEmpty(title) ? rendering : rendering.withAttribute("title", encode(title));
    }

    /*
     * TODO: Improve this code. It supports only Markdown links without anchors, and it assumes Markdown becomes HMTL.
     */
    String resolveLink(String originalLink) {
        if (originalLink.contains("://")) {
            return originalLink;
        }
        for (String extension : MARKDOWN_EXTENSIONS) {
            if (originalLink.toLowerCase().endsWith(extension)) {
                return originalLink.substring(0, originalLink.length() - extension.length()) + "html";
            }
        }
        return originalLink;
    }
}

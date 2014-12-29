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

import org.pegdown.ast.*;

/**
 * Finds the first level 1 title in a Markdown document.
 */
class TitleFinder extends AbstractVisitor {

    private final StringBuilder buffer;
    private boolean isFound;
    private boolean isBuffering;

    public TitleFinder() {
        buffer = new StringBuilder();
        isFound = false;
        isBuffering = false;
    }

    @Override
    public void visit(RootNode node) {
        visitChildren(node);
    }

    private void visitChildren(SuperNode node) {
        for (Node child : node.getChildren()) {
            if (isFound) {
                return;
            }
            child.accept(this);
        }
    }

    @Override
    public void visit(HeaderNode node) {
        if (node.getLevel() == 1) {
            isBuffering = true;
            visitChildren(node);
            isBuffering = false;
            isFound = true;
        }
    }

    @Override
    public void visit(TextNode node) {
        if (isBuffering) {
            buffer.append(node.getText());
        }
    }

    public String extractTitle(RootNode rootNode) {
        rootNode.accept(this);
        return buffer.toString();
    }
}

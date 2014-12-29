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

package nl.ulso.magisto.document;

import java.nio.file.Path;

public class DummyDocumentSupportFactory implements DocumentSupportFactory {

    private boolean customTemplateChanged = false;

    @Override
    public DocumentLoader createDocumentLoader(Path sourceRoot) {
        return new DummyDocumentLoader(sourceRoot);
    }

    @Override
    public DocumentConverter createDocumentConverter(Path sourceRoot, Path targetRoot) {
        return new DummyDocumentConverter(sourceRoot, targetRoot, customTemplateChanged);
    }

    public void setCustomTemplateChanged() {
        this.customTemplateChanged = true;
    }
}

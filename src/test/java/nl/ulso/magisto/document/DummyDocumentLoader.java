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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DummyDocumentLoader implements DocumentLoader {

    @Override
    public Set<String> getSupportedExtensions() {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList("convert")));
    }

    @Override
    public boolean supports(Path path) {
        return path.getFileName().toString().endsWith(".convert");
    }

    @Override
    public Document loadDocument(Path path) throws IOException {
        return new Document() {
            @Override
            public String getTitle() {
                return "Dummy document";
            }

            @Override
            public String toHtml() {
                return "";
            }
        };
    }
}

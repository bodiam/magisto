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

public class DummyDocument implements Document {

    private final String fileName;

    public DummyDocument(Path path) {
        fileName = path.getFileName().toString();
    }

    @Override
    public String getTitle() {
        return fileName;
    }

    @Override
    public String toHtml() {
        return "<html><body>" + fileName + "</body></html>";
    }

    @Override
    public History getHistory() {
        return new DummyHistory();
    }
}

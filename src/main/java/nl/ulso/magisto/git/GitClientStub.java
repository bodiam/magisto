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

package nl.ulso.magisto.git;

import nl.ulso.magisto.document.Commit;
import nl.ulso.magisto.document.History;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static java.util.Collections.emptyList;
import static nl.ulso.magisto.document.Commit.DEFAULT_COMMIT;

/**
 *
 */
public class GitClientStub implements GitClient {
    @Override
    public History getHistory(Path path) {
        return new History() {
            @Override
            public List<Commit> getCommits() throws IOException {
                return emptyList();
            }

            @Override
            public Commit getLastCommit() throws IOException {
                return DEFAULT_COMMIT;
            }
        };
    }
}

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

package nl.ulso.magisto.git;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * Dummy implementation used then the source directory is not an actual Git repository.
 */
public class DummyGitClient implements GitClient {

    private static final History DEFAULT_HISTORY = new History() {
        @Override
        public List<Commit> getCommits() throws IOException {
            return Collections.emptyList();
        }

        @Override
        public Commit getLastCommit() throws IOException {
            return Commit.DEFAULT_COMMIT;
        }
    };

    @Override
    public History getHistory(Path path) {
        return DEFAULT_HISTORY;
    }
}

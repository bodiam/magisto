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

import nl.ulso.magisto.document.History;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * {@link GitClient} implementation based on JGit.
 */
public class JGitClient implements GitClient {

    private final Git git;

    public JGitClient(String sourceDirectory) throws IOException {
        git = Git.open(new File(sourceDirectory));
    }

    @Override
    /*
     * The logic to actually go out into the history is in the history itself. This way it's only performed if the page
     * template is actually accessing it.
     */
    public History getHistory(Path path) {
        return new JGitHistory(git, path);
    }
}

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
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static nl.ulso.magisto.io.Paths.requireRelativePath;

class JGitHistory implements History {

    private final Git git;
    private final String path;

    private Commit lastCommit;
    private List<Commit> commits;

    JGitHistory(Git git, Path path) {
        requireRelativePath(path);
        this.git = git;
        this.path = path.toString();
        this.lastCommit = null;
        this.commits = null;
    }

    @Override
    public List<Commit> getCommits() throws IOException {
        if (commits == null) {
            commits = new ArrayList<>();
            final Iterator<RevCommit> iterator = createCommitIterator();
            while (iterator.hasNext()) {
                commits.add(createCommit(iterator.next()));
            }
        }
        return unmodifiableList(commits);
    }

    @Override
    public Commit getLastCommit() throws IOException {
        if (lastCommit == null) {
            final Iterator<RevCommit> commits = createCommitIterator();
            if (commits.hasNext()) {
                lastCommit = createCommit(commits.next());
            } else {
                lastCommit = Commit.DEFAULT_COMMIT;
            }
        }
        return lastCommit;
    }

    private Iterator<RevCommit> createCommitIterator() throws IOException {
        final Iterator<RevCommit> commits;
        try {
            commits = git.log().addPath(path).call().iterator();
        } catch (GitAPIException e) {
            throw new IOException(e);
        }
        return commits;
    }

    private Commit createCommit(RevCommit revCommit) {
        return new Commit(
                revCommit.getId().name(),
                new Date((long) revCommit.getCommitTime() * 1000l),
                revCommit.getCommitterIdent().getName(),
                revCommit.getCommitterIdent().getEmailAddress(),
                revCommit.getShortMessage(),
                revCommit.getFullMessage());
    }

}

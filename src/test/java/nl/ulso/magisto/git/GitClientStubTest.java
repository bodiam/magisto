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

import nl.ulso.magisto.document.Commit;
import nl.ulso.magisto.document.History;
import org.junit.Test;

import java.util.Date;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GitClientStubTest {

    @Test
    public void testStubbedHistoryCommits() throws Exception {
        final History history = new GitClientStub().getHistory(createPath("foo"));
        assertNotNull(history.getCommits());
        assertEquals(0, history.getCommits().size());
    }

    @Test
    public void testStubbedLastCommit() throws Exception {
        final History history = new GitClientStub().getHistory(createPath("foo"));
        final Commit commit = history.getLastCommit();
        assertNotNull(commit);
        assertEquals("UNKNOWN", commit.getId());
        assertEquals("UNKNOWN", commit.getShortId());
        assertEquals("UNKNOWN", commit.getCommitter());
        assertEquals("UNKNOWN", commit.getEmailAddress());
        assertEquals("-", commit.getShortMessage());
        assertEquals("-", commit.getFullMessage());
        assertEquals(new Date(0), commit.getTimestamp());
    }
}
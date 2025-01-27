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

import org.junit.Test;

import static nl.ulso.magisto.io.Paths.createPath;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JGitClientTest {

    @Test
    public void testCommitAvailable() throws Exception {
        final History history = new JGitClient(System.getProperty("user.dir")).getHistory(createPath("pom.xml"));
        assertNotNull(history);
        assertNotNull(history.getLastCommit());
    }

    @Test
    public void testChangelog() throws Exception {
        final History history = new JGitClient(System.getProperty("user.dir")).getHistory(createPath("pom.xml"));
        assertNotNull(history);
        assertNotNull(history.getCommits());
        assertTrue(history.getCommits().size() > 0);
    }
}
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

import java.util.Date;

/**
 * Exposes information on a single commit; it's passed in the page model for each file.
 */
public class Commit {
    public static final Commit DEFAULT_COMMIT = new Commit("UNKNOWN", new Date(0), "UNKNOWN", "UNKNOWN", "-", "-");

    private final String id;
    private final Date timestamp;
    private final String committer;
    private final String emailAddress;
    private final String shortMessage;
    private final String fullMessage;


    public Commit(String id, Date timestamp, String committer, String emailAddress, String shortMessage,
                  String fullMessage) {
        this.id = id;
        this.timestamp = timestamp;
        this.committer = committer;
        this.emailAddress = emailAddress;
        this.shortMessage = shortMessage;
        this.fullMessage = fullMessage;
    }

    public String getId() {
        return id;
    }

    public String getShortId() {
        return id.substring(0, 7);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getCommitter() {
        return committer;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String getFullMessage() {
        return fullMessage;
    }
}

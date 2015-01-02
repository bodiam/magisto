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

package nl.ulso.magisto.action;

/**
 * Specifies the action type, used mostly for keeping and logging statistics.
 */
public enum ActionType {

    SKIP_SOURCE("Skipped", "source"),
    SKIP_STATIC("Skipped", "static"),
    COPY_SOURCE("Copied", "source"),
    COPY_STATIC("Copied", "static"),
    DELETE_TARGET("Deleted", "target"),
    CONVERT_SOURCE("Converted", "source");

    private final String pastTenseVerb;
    private final String fileType;

    private ActionType(String pastTenseVerb, String fileType) {
        this.pastTenseVerb = pastTenseVerb;
        this.fileType = fileType;
    }

    public String getPastTenseVerb() {
        return pastTenseVerb;
    }

    public String getFileType() {
        return fileType;
    }
}

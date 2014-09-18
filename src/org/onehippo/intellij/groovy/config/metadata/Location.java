/*
 * Copyright 2014 Hippo B.V. (http://www.onehippo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onehippo.intellij.groovy.config.metadata;

import com.google.common.base.Strings;

/**
 *
 * @version "$Id$"
 */
public enum Location {

    REGISTRY("registry", "/hippo:configuration/hippo:update/hippo:registry"),
    HISTORY("history", "/hippo:configuration/hippo:update/hippo:history"),
    QUEUE("queue", "/hippo:configuration/hippo:update/hippo:queue");

    private final String name;
    private final String path;

    Location(final String name, final String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public static Location locationForName(final String name) {
        if (Strings.isNullOrEmpty(name)) {
            return REGISTRY;
        }
        if (name.equalsIgnoreCase(HISTORY.getName())) {
            return HISTORY;
        } else if (name.equalsIgnoreCase(QUEUE.getName())) {
            return QUEUE;
        }
        return REGISTRY;
    }

    public static class Constants {
        public static final String FETCH_QUERY = "hippo:configuration/hippo:update//element(*, hipposys:updaterinfo)";

        private Constants() {
        }
    }
}

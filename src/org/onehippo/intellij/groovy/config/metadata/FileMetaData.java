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

/**
 * Here we store query stuff etc.
 *
 * @version "$Id$"
 */
public class FileMetaData {

    private Location location = Location.REGISTRY;
    private ExecuteType type = ExecuteType.QUERY;

    private String name;
    private int batchSize = 10;
    private int throttle = 1000;
    private String script;

    public Location getLocation() {
        if (location == null) {
            location = Location.REGISTRY;
        }
        return location;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ExecuteType getType() {
        if (type == null) {
            type = ExecuteType.QUERY;
        }
        return type;
    }

    public void setType(final ExecuteType type) {
        this.type = type;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(final int batchSize) {
        this.batchSize = batchSize;
    }

    public int getThrottle() {
        return throttle;
    }

    public void setThrottle(final int throttle) {
        this.throttle = throttle;
    }

    public String getScript() {
        return script;
    }

    public void setScript(final String script) {
        this.script = script;
    }
}

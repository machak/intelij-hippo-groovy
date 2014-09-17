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

package org.onehippo.intellij.groovy.config.metadata.gui;

public class FileDialogData {
    private String nodeId;
    private String nodePath;
    private String filePath;
    private String scriptName;
    private String queryScript;
    private String pathScript;
    private String queryOrPath;
    private String location;
    private String content;
    private int throttle;
    private int batchSize;
    private boolean dryRun;

    public FileDialogData() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(final String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(final String nodePath) {
        this.nodePath = nodePath;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FileDialogData{");
        sb.append("scriptName='").append(scriptName).append('\'');
        sb.append(", queryScript='").append(queryScript).append('\'');
        sb.append(", pathScript='").append(pathScript).append('\'');
        sb.append(", queryOrPath='").append(queryOrPath).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append(", throttle=").append(throttle);
        sb.append(", batchSize=").append(batchSize);
        sb.append(", dryRun=").append(dryRun);
        sb.append('}');
        return sb.toString();
    }

    public String getPathScript() {
        return pathScript;
    }

    public void setPathScript(final String pathScript) {
        this.pathScript = pathScript;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(final boolean dryRun) {
        this.dryRun = dryRun;
    }

    public String getQueryOrPath() {
        return queryOrPath;
    }

    public void setQueryOrPath(final String queryOrPath) {
        this.queryOrPath = queryOrPath;
    }

    public int getThrottle() {
        return throttle;
    }

    public void setThrottle(final int throttle) {
        this.throttle = throttle;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(final int batchSize) {
        this.batchSize = batchSize;
    }


    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(final String scriptName) {
        this.scriptName = scriptName;
    }

    public String getQueryScript() {
        return queryScript;
    }

    public void setQueryScript(final String queryScript) {
        this.queryScript = queryScript;
    }


}